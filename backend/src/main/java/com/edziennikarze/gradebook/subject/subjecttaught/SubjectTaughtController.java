package com.edziennikarze.gradebook.subject.subjecttaught;

import static com.edziennikarze.gradebook.user.Role.*;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.user.dto.User;

@RestController
@RequestMapping("/subject-taught")
@RequiredArgsConstructor
public class SubjectTaughtController {

    private final SubjectTaughtService subjectTaughtService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<SubjectTaught> createSubjectTaught(@RequestBody Mono<SubjectTaught> subjectTaught) {
        return subjectTaughtService.createSubjectTaught(subjectTaught);
    }

    @PostMapping("/bulk")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<SubjectTaught> createSubjectsTaught(@RequestBody Flux<SubjectTaught> subjectsTaught) {
        return subjectTaughtService.createSubjectsTaught(subjectsTaught);
    }

    @GetMapping("/all")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<Subject> getAllSubjectsTaught() {
        return subjectTaughtService.getAllSubjectsTaught();
    }

    @GetMapping("/teacher/{teacherId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, TEACHER, GUARDIAN, STUDENT})
    public Flux<Subject> getSubjectsTaughtByTeacher(@PathVariable UUID teacherId) {
        return subjectTaughtService.getSubjectsTaughtByTeacher(teacherId);
    }

    @GetMapping("/subject/{subjectId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, TEACHER, GUARDIAN, STUDENT})
    public Flux<User> getTeachersTeachingSubject(@PathVariable UUID subjectId) {
        return subjectTaughtService.getTeachersTeachingSubject(subjectId);
    }
}
