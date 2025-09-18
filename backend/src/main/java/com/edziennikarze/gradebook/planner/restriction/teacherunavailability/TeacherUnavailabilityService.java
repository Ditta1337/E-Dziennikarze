package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.CollisionException;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TeacherUnavailabilityService {

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    public Mono<TeacherUnavailability> createTeacherUnavailability(Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityMono
                .flatMap(this::validateNoCollision)
                .flatMap(teacherUnavailabilityRepository::save);
    }

    public Flux<TeacherUnavailability> getAllTeachersUnavailabilities(UUID teacherId) {
        return teacherUnavailabilityRepository.findAllByTeacherId(teacherId);
    }

    public Mono<TeacherUnavailability> updateTeacherUnavailability(Mono<TeacherUnavailability> teacherUnavailabilityMono) {
        return teacherUnavailabilityMono.flatMap(teacherUnavailability -> teacherUnavailabilityRepository.findById(teacherUnavailability.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("TeacherUnavailability with id " + teacherUnavailability.getId() + " not found")))
                .flatMap(existingTeacherUnavailability -> {
                    existingTeacherUnavailability.setTeacherId(teacherUnavailability.getTeacherId());
                    existingTeacherUnavailability.setStartTime(teacherUnavailability.getStartTime());
                    existingTeacherUnavailability.setEndTime(teacherUnavailability.getEndTime());
                    existingTeacherUnavailability.setWeekDay(teacherUnavailability.getWeekDay());
                    return teacherUnavailabilityRepository.save(existingTeacherUnavailability);
                }));
    }

    public Mono<Void> deleteTeacherUnavailability(UUID teacherUnavailabilityId) {
        return teacherUnavailabilityRepository.deleteById(teacherUnavailabilityId);
    }

    private Mono<TeacherUnavailability> validateNoCollision(TeacherUnavailability unavailability) {
        return teacherUnavailabilityRepository.existsByTeacherIdAndWeekDayAndStartTimeBeforeAndEndTimeAfter(unavailability.getTeacherId(),
                        unavailability.getWeekDay(), unavailability.getEndTime(), unavailability.getStartTime())
                .flatMap(collisionExists -> {
                    if ( Boolean.TRUE.equals(collisionExists) ) {
                        return Mono.error(new CollisionException("Teacher unavailability collides with an existing entry"));
                    }
                    return Mono.just(unavailability);
                });
    }
}
