package com.edziennikarze.gradebook.lesson.planned;

import java.util.UUID;

import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlannedLessonService {

    private final PlannedLessonRepository plannedLessonRepository;

    private final AssignedLessonRepository assignedLessonRepository;

    public Mono<PlannedLesson> createPlannedLesson(Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonMono.flatMap(plannedLessonRepository::save);
    }

    public Flux<PlannedLesson> getAllPlannedLessons() {
        return plannedLessonRepository.findAll();
    }

    public Flux<PlannedLesson> getAllPlannedLessonsByGroupId(UUID groupId) {
        return plannedLessonRepository.findAllByGroupId(groupId);
    }

    public Flux<PlannedLesson> getAllPlannedLessonsBySubjectId(UUID subjectId) {
        return plannedLessonRepository.findAllBySubjectId(subjectId);
    }

    public Flux<PlannedLesson> getAllPlannedLessonsByTeacherId(UUID teacherId) {
        return plannedLessonRepository.findAllByTeacherId(teacherId);
    }

    public Mono<PlannedLesson> updatePlannedLesson(Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonMono.flatMap(plannedLesson -> plannedLessonRepository.findById(plannedLesson.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("PlannedLesson with id " + plannedLesson.getId() + " not found")))
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
}
