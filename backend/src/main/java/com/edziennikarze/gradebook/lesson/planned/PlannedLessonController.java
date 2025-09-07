package com.edziennikarze.gradebook.lesson.planned;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/planned-lesson")
@AllArgsConstructor
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

    @GetMapping("/all/student/{studentId}/from/{dateFrom}/to{dateTo}")
    public Flux<PlannedLesson> getAllPannedLessonsByGroupAndBetweenDates(@PathVariable UUID studentId, @PathVariable LocalDate dateFrom, @PathVariable LocalDate dateTo) {
        return plannedLessonService.getAllAssignedLessonsByStudentIdBetweenDates(studentId, dateFrom, dateTo);
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
