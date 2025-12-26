package com.edziennikarze.gradebook.lesson;

import com.edziennikarze.gradebook.lesson.dto.DeleteRequest;
import com.edziennikarze.gradebook.lesson.dto.Lesson;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/all/student/{studentId}/from/{dateFrom}/to/{dateTo}")
    public Flux<Lesson> getAllStudentLessonsBetweenDates(@PathVariable UUID studentId, @PathVariable LocalDate dateFrom, @PathVariable LocalDate dateTo) {
        return lessonService.getAllLessonsByStudentIdBetweenDates(studentId, dateFrom, dateTo);
    }

    @GetMapping("/all/teacher/{teacherId}/from/{dateFrom}/to/{dateTo}")
    public Flux<Lesson> getAllTeacherLessonsBetweenDates(@PathVariable UUID teacherId, @PathVariable LocalDate dateFrom, @PathVariable LocalDate dateTo) {
        return lessonService.getAllLessonsByTeacherIdBetweenDates(teacherId, dateFrom, dateTo);
    }

    @GetMapping("/all/group/{groupId}/from/{dateFrom}/to/{dateTo}")
    public Flux<Lesson> getAllGroupLessonsBetweenDates(@PathVariable UUID groupId, @PathVariable LocalDate dateFrom, @PathVariable LocalDate dateTo) {
        return lessonService.getAllLessonsByGroupIdBetweenDates(groupId, dateFrom, dateTo);
    }

    @PostMapping("/delete")
    public Mono<Void> deleteLessons(@RequestBody Mono<DeleteRequest> toDelete) {
        return lessonService.deleteLessons(toDelete);
    }

    @PostMapping
    public Flux<Lesson> createLessons(@RequestBody Flux<Lesson> lessonFlux) {
        return lessonService.createLessons(lessonFlux);
    }

    @PutMapping
    public Flux<Lesson> updateLessons(@RequestBody Flux<Lesson> lessonFlux) {
        return lessonService.updateLessons(lessonFlux);
    }
}