package com.edziennikarze.gradebook.subject.subjecttaught;

import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/subject-taught")
@RequiredArgsConstructor
public class SubjectTaughtController {

    private final SubjectTaughtService subjectTaughtService;

    @PostMapping
    public Mono<SubjectTaught> createSubjectTaught(@RequestBody Mono<SubjectTaught> subjectTaught) {
        return subjectTaughtService.createSubjectTaught(subjectTaught);
    }

    @PostMapping("/bulk")
    public Flux<SubjectTaught> createSubjectsTaught(@RequestBody Flux<SubjectTaught> subjectsTaught) {
        return subjectTaughtService.createSubjectsTaught(subjectsTaught);
    }

    @GetMapping("/all")
    public Flux<Subject> getAllSubjectsTaught() {
        return subjectTaughtService.getAllSubjectsTaught();
    }

    @GetMapping("/teacher/{teacherId}")
    public Flux<Subject> getSubjectsTaughtByTeacher(@PathVariable UUID teacherId) {
        return subjectTaughtService.getSubjectsTaughtByTeacher(teacherId);
    }

    @GetMapping("/subject/{subjectId}")
    public Flux<User> getTeachersTeachingSubject(@PathVariable UUID subjectId) {
        return subjectTaughtService.getTeachersTeachingSubject(subjectId);
    }

    @DeleteMapping("/teacher/{teacherId}/subject/{subjectId}")
    public Mono<Void> deleteSubjectTaught(@PathVariable UUID teacherId, @PathVariable UUID subjectId) {
        return subjectTaughtService.deleteByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}