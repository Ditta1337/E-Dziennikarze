package com.edziennikarze.gradebook.lesson.planned;

import static com.edziennikarze.gradebook.user.Role.*;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/planned-lesson")
@AllArgsConstructor
public class PlannedLessonController {

    private final PlannedLessonService plannedLessonService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<PlannedLesson> createPlannedLesson(@RequestBody Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonService.createPlannedLesson(plannedLessonMono);
    }

    @GetMapping("/all")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<PlannedLesson> getAllPlannedLessons() {
        return plannedLessonService.getAllPlannedLessons();
    }

    @GetMapping("/all/group/{groupId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<PlannedLesson> getAllPlannedLessonByGroup(@PathVariable UUID groupId) {
        return plannedLessonService.getAllPlannedLessonsByGroupId(groupId);
    }

    @GetMapping("/all/subject/{subjectId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<PlannedLesson> getAllPlannedLessonBySubject(@PathVariable UUID subjectId) {
        return plannedLessonService.getAllPlannedLessonsBySubjectId(subjectId);
    }

    @GetMapping("/all/teacher/{teacherId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Flux<PlannedLesson> getAllPlannedLessonByTeacher(@PathVariable UUID teacherId) {
        return plannedLessonService.getAllPlannedLessonsByTeacherId(teacherId);
    }

    @PutMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<PlannedLesson> updatePlannedLesson(@RequestBody Mono<PlannedLesson> plannedLessonMono) {
        return plannedLessonService.updatePlannedLesson(plannedLessonMono);
    }
}
