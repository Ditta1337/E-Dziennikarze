package com.edziennikarze.gradebook.lesson.planned;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PlannedLessonService {

    private final PlannedLessonRepository plannedLessonRepository;

    private final AssignedLessonRepository assignedLessonRepository;

    private final StudentGroupRepository studentGroupRepository;

    public Mono<PlannedLesson> createPlannedLesson(Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonMono.flatMap(plannedLessonRepository::save);
    }

    public Flux<PlannedLesson> getAllPlannedLessons() {
        return plannedLessonRepository.findAll();
    }

    public Flux<PlannedLesson> getAllPlannedLessonsByGroupId(UUID groupId) {
        return plannedLessonRepository.findAllByGroupId(groupId);
    }

    public Flux<PlannedLesson> getAllAssignedLessonsByStudentIdBetweenDates(UUID studentId, LocalDate from, LocalDate to) {
        return studentGroupRepository.findAllByStudentId(studentId)
                .map(StudentGroup::getGroupId)
                .flatMap(groupId -> getAllAssignedLessonsByGroupIdBetweenDates(groupId, from, to));
    }

    public Flux<PlannedLesson> getAllPlannedLessonsBySubjectId(UUID subjectId) {
        return plannedLessonRepository.findAllBySubjectId(subjectId);
    }

    public Flux<PlannedLesson> getAllPlannedLessonsByTeacherId(UUID teacherId) {
        return plannedLessonRepository.findAllByTeacherId(teacherId);
    }

    public Mono<PlannedLesson> updatePlannedLesson(Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonMono.flatMap(plannedLesson -> plannedLessonRepository.findById(plannedLesson.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Planned lesson with id " + plannedLesson.getId() + " not found")))
                .flatMap(existingPlannedLesson -> {
                    existingPlannedLesson.setSubjectId(plannedLesson.getSubjectId());
                    existingPlannedLesson.setTeacherId(plannedLesson.getTeacherId());
                    existingPlannedLesson.setGroupId(plannedLesson.getGroupId());
                    existingPlannedLesson.setActive(plannedLesson.isActive());
                    existingPlannedLesson.setStartTime(plannedLesson.getStartTime());
                    existingPlannedLesson.setEndTime(plannedLesson.getEndTime());
                    existingPlannedLesson.setRoomId(plannedLesson.getRoomId());
                    existingPlannedLesson.setWeekDay(plannedLesson.getWeekDay());
                    return plannedLessonRepository.save(existingPlannedLesson);
                }));
    }

    private Flux<PlannedLesson> getAllAssignedLessonsByGroupIdBetweenDates(UUID groupId, LocalDate from, LocalDate to) {
        return assignedLessonRepository.findAllByDateBetween(from, to)
                .map(AssignedLesson::getPlannedLessonId)
                .collectList()
                .flatMapMany(plannedLessonRepository::findAllById)
                .filter(plannedLesson -> plannedLesson.getGroupId().equals(groupId));
    }
}
