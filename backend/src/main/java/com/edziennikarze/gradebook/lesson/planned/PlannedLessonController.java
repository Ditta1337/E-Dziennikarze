package com.edziennikarze.gradebook.lesson.planned;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/planned-lesson")
@AllArgsConstructor
public class PlannedLessonController {

    private final PlannedLessonService plannedLessonService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<PlannedLesson> createPlannedLesson(@RequestBody Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonService.createPlannedLesson(plannedLessonMono);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<PlannedLesson> getAllPlannedLessons() {
        return plannedLessonService.getAllPlannedLessons();
    }

    @GetMapping("/all/group/{groupId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<PlannedLesson> getAllPlannedLessonByGroup(@PathVariable UUID groupId) {
        return plannedLessonService.getAllPlannedLessonsByGroupId(groupId);
    }

    @GetMapping("/all/subject/{subjectId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<PlannedLesson> getAllPlannedLessonBySubject(@PathVariable UUID subjectId) {
        return plannedLessonService.getAllPlannedLessonsBySubjectId(subjectId);
    }

    @GetMapping("/all/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'TEACHER')")
    public Flux<PlannedLesson> getAllPlannedLessonByTeacher(@PathVariable UUID teacherId) {
        return plannedLessonService.getAllPlannedLessonsByTeacherId(teacherId);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<PlannedLesson> updatePlannedLesson(@RequestBody Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonService.updatePlannedLesson(plannedLessonMono);
    }
}