package com.edziennikarze.gradebook.subject.subjecttaught;

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
    public Flux<SubjectTaught> getAllSubjectsTaught() {
        return subjectTaughtService.getAllSubjectsTaught();
    }

    @GetMapping("/teacher/{teacherId}")
    public Flux<SubjectTaught> getSubjectsTaughtByTeacher(@PathVariable UUID teacherId) {
        return subjectTaughtService.getSubjectsTaughtByTeacher(teacherId);
    }

    @GetMapping("/subject/{subjectId}")
    public Flux<SubjectTaught> getSubjectsTaughtBySubject(@PathVariable UUID subjectId) {
        return subjectTaughtService.getSubjectsTaughtBySubject(subjectId);
    }

    @DeleteMapping("/{subjectTaughtId}")
    public Mono<Void> deleteSubjectTaught(@PathVariable UUID subjectTaughtId) {
        return subjectTaughtService.deleteSubjectTaught(subjectTaughtId);
    }

    @DeleteMapping("/teacher/{teacherId}")
    public Mono<Void> deleteSubjectsTaughtByTeacher(@PathVariable UUID teacherId) {
        return subjectTaughtService.deleteSubjectsTaughtByTeacher(teacherId);
    }
}
