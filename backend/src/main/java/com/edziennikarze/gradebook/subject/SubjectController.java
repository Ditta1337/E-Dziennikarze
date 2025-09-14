package com.edziennikarze.gradebook.subject;

import static com.edziennikarze.gradebook.user.Role.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<Subject> createSubject(@RequestBody Mono<Subject> subject) {
        return subjectService.createSubject(subject);
    }

    @GetMapping("/all")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @DeleteMapping("/{subjectId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<Void> deleteSubject(@PathVariable UUID subjectId) {
        return subjectService.deleteSubject(subjectId);
    }
}
