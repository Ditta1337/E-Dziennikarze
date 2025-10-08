package com.edziennikarze.gradebook.lesson.planned;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/planned-lesson")
@RequiredArgsConstructor
public class PlannedLessonController {

    private final PlannedLessonService plannedLessonService;

    @PostMapping
    public Mono<PlannedLesson> createPlannedLesson(@RequestBody Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonService.createPlannedLesson(plannedLessonMono);
    }

    @GetMapping("/all")
    public Flux<PlannedLesson> getAllPlannedLessons() {
        return plannedLessonService.getAllPlannedLessons();
    }

    @GetMapping("/all/group/{groupId}")
    public Flux<PlannedLesson> getAllPlannedLessonByGroup(@PathVariable UUID groupId) {
        return plannedLessonService.getAllPlannedLessonsByGroupId(groupId);
    }

    @GetMapping("/all/subject/{subjectId}")
    public Flux<PlannedLesson> getAllPlannedLessonBySubject(@PathVariable UUID subjectId) {
        return plannedLessonService.getAllPlannedLessonsBySubjectId(subjectId);
    }

    @GetMapping("/all/teacher/{teacherId}")
    public Flux<PlannedLesson> getAllPlannedLessonByTeacher(@PathVariable UUID teacherId) {
        return plannedLessonService.getAllPlannedLessonsByTeacherId(teacherId);
    }

    @PutMapping
    public Mono<PlannedLesson> updatePlannedLesson(@RequestBody Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonService.updatePlannedLesson(plannedLessonMono);
    }
}