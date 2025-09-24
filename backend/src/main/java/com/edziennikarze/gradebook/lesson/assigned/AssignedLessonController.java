package com.edziennikarze.gradebook.lesson.assigned;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/assigned-lesson")
@AllArgsConstructor
public class AssignedLessonController {

    private final AssignedLessonService assignedLessonService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<AssignedLesson> createAssignedLesson(@RequestBody Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonService.createAssignedLesson(assignedLessonMono);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<AssignedLesson> getAllAssignedLessons() {
        return assignedLessonService.getAllAssignedLessons();
    }

    @GetMapping("/all/cancelled")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<AssignedLesson> getAllAssignedLessonsCancelled() {
        return assignedLessonService.getAllCancelledAssignedLessons();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<AssignedLesson> updateAssignedLesson(@RequestBody Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonService.updateAssignedLesson(assignedLessonMono);
    }
}