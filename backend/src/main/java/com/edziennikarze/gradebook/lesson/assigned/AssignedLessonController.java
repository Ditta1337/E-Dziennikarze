package com.edziennikarze.gradebook.lesson.assigned;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/assigned-lesson")
@AllArgsConstructor
public class AssignedLessonController {

    private final AssignedLessonService assignedLessonService;

    @PostMapping
    public Mono<AssignedLesson> createAssignedLesson(@RequestBody Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonService.createAssignedLesson(assignedLessonMono);
    }

    @GetMapping("/all")
    public Flux<AssignedLesson> getAllAssignedLessons() {
        return assignedLessonService.getAllAssignedLessons();
    }

    @GetMapping("/all/cancelled")
    public Flux<AssignedLesson> getAllAssignedLessonsCancelled() {
        return assignedLessonService.getAllCancelledAssignedLessons();
    }

    @PutMapping
    public Mono<AssignedLesson> updateAssignedLesson(@RequestBody Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonService.updateAssignedLesson(assignedLessonMono);
    }
}
