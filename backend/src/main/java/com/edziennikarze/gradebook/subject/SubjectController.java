package com.edziennikarze.gradebook.subject;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Subject> createSubject(@RequestBody Mono<Subject> subject) {
        return subjectService.createSubject(subject);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @DeleteMapping("/{subjectId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Void> deleteSubject(@PathVariable UUID subjectId) {
        return subjectService.deleteSubject(subjectId);
    }
}
