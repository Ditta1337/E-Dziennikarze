package com.edziennikarze.gradebook.lesson;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/lesson")
@AllArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/all/student/{studentId}/from/{dateFrom}/to/{dateTo}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Lesson> getAllStudentLessonsBetweenDates(@PathVariable UUID studentId, @PathVariable LocalDate dateFrom, @PathVariable LocalDate dateTo) {
        return lessonService.getAllLessonsByStudentIdBetweenDates(studentId, dateFrom, dateTo);
    }

    @GetMapping("/all/teacher/{teacherId}/from/{dateFrom}/to/{dateTo}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Lesson> getAllTeacherLessonsBetweenDates(@PathVariable UUID teacherId, @PathVariable LocalDate dateFrom, @PathVariable LocalDate dateTo) {
        return lessonService.getAllLessonsByTeacherIdBetweenDates(teacherId, dateFrom, dateTo);
    }
}