package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.CollisionException;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.user.Role;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TeacherUnavailabilityService {

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    private final LoggedInUserService loggedInUserService;

    public Mono<TeacherUnavailability> createTeacherUnavailability(Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityMono.flatMap(this::validateNoCollision)
                .flatMap(teacherUnavailabilityRepository::save);
    }

    public Flux<TeacherUnavailability> getAllTeachersUnavailabilities(UUID teacherId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(teacherId, Role.PRINCIPAL, Role.OFFICE_WORKER)
                .thenMany(teacherUnavailabilityRepository.findAllByTeacherId(teacherId));
    }

    public Mono<TeacherUnavailability> updateTeacherUnavailability(Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityMono.flatMap(this::validateNoCollisionForUpdate)
                .flatMap(unavailability -> loggedInUserService.isSelfOrAllowedRoleElseThrow(unavailability.getTeacherId(), Role.PRINCIPAL, Role.OFFICE_WORKER)
                        .then(teacherUnavailabilityRepository.findById(unavailability.getId())
                                .switchIfEmpty(
                                        Mono.error(new ResourceNotFoundException("TeacherUnavailability with id " + unavailability.getId() + " not found")))
                                .flatMap(existingTeacherUnavailability -> {
                                    existingTeacherUnavailability.setTeacherId(unavailability.getTeacherId());
                                    existingTeacherUnavailability.setStartTime(unavailability.getStartTime());
                                    existingTeacherUnavailability.setEndTime(unavailability.getEndTime());
                                    existingTeacherUnavailability.setWeekDay(unavailability.getWeekDay());
                                    return teacherUnavailabilityRepository.save(existingTeacherUnavailability);
                                })));
    }

    public Mono<Void> deleteTeacherUnavailability(UUID teacherUnavailabilityId) {
        return teacherUnavailabilityRepository.findById(teacherUnavailabilityId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("TeacherUnavailability with id " + teacherUnavailabilityId + " not found")))
                .flatMap(unavailability -> loggedInUserService.isSelfOrAllowedRoleElseThrow(unavailability.getTeacherId(), Role.PRINCIPAL, Role.OFFICE_WORKER)
                        .then(teacherUnavailabilityRepository.deleteById(teacherUnavailabilityId)));
    }

    private Mono<TeacherUnavailability> validateNoCollision(TeacherUnavailability unavailability) {
        return teacherUnavailabilityRepository.existsByTeacherIdAndWeekDayAndStartTimeBeforeAndEndTimeAfter(unavailability.getTeacherId(),
                        unavailability.getWeekDay(), unavailability.getEndTime(), unavailability.getStartTime())
                .flatMap(collisionExists -> {
                    if (Boolean.TRUE.equals(collisionExists)) {
                        return Mono.error(new CollisionException("Teacher unavailability collides with an existing entry"));
                    }
                    return Mono.just(unavailability);
                });
    }

    private Mono<TeacherUnavailability> validateNoCollisionForUpdate(TeacherUnavailability unavailability) {
        return teacherUnavailabilityRepository.getAllByTeacherIdAndWeekDayAndStartTimeBeforeAndEndTimeAfter(unavailability.getTeacherId(),
                        unavailability.getWeekDay(), unavailability.getEndTime(), unavailability.getStartTime())
                .collectList()
                .flatMap(collidingUnavailabilities -> {
                    if (validateCollidingListWithUnavailability(collidingUnavailabilities, unavailability)) {
                        return Mono.just(unavailability);
                    }
                    return Mono.error(new CollisionException("Teacher unavailability collides with an existing entry"));
                });
    }

    private boolean validateCollidingListWithUnavailability(List<TeacherUnavailability> collidingUnavailabiliteis, TeacherUnavailability unavailability) {
        return collidingUnavailabiliteis.isEmpty() || collidingUnavailabiliteis.size() == 1 && collidingUnavailabiliteis.getFirst().getId().equals(unavailability.getId());
    }
}
