package com.edziennikarze.gradebook.lesson.assigned;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/assigned-lesson")
@RequiredArgsConstructor
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