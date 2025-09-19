package com.edziennikarze.gradebook.lesson.assigned;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AssignedLessonService {

    private final AssignedLessonRepository assignedLessonRepository;

    public Mono<AssignedLesson> createAssignedLesson(Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonMono.flatMap(assignedLessonRepository::save);
    }

    public Flux<AssignedLesson> getAllAssignedLessons() {
        return assignedLessonRepository.findAll();
    }

    public Flux<AssignedLesson> getAllCancelledAssignedLessons() {
        return assignedLessonRepository.findAllByCancelled(true);
    }

    public Mono<AssignedLesson> updateAssignedLesson(Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonMono.flatMap(assignedLesson -> assignedLessonRepository.findById(assignedLesson.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Assigned lesson with id " + assignedLesson.getId() + " not found")))
                .flatMap(existingAssignedLesson -> {
                    existingAssignedLesson.setPlannedLessonId(assignedLesson.getPlannedLessonId());
                    existingAssignedLesson.setDate(assignedLesson.getDate());
                    existingAssignedLesson.setCancelled(assignedLesson.isCancelled());
                    existingAssignedLesson.setModified(assignedLesson.isModified());
                    return assignedLessonRepository.save(existingAssignedLesson);
                }));
    }
}
