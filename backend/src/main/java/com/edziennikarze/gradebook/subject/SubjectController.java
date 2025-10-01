package com.edziennikarze.gradebook.subject;

import lombok.RequiredArgsConstructor;
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
    public Mono<Subject> createSubject(@RequestBody Mono<Subject> subject) {
        return subjectService.createSubject(subject);
    }

    @GetMapping("/all")
    public Flux<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @DeleteMapping("/{subjectId}")
    public Mono<Void> deleteSubject(@PathVariable UUID subjectId) {
        return subjectService.deleteSubject(subjectId);
    }
}
