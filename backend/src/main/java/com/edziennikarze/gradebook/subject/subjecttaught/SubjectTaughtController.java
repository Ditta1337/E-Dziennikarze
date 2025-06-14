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

    @GetMapping("/teacher/{teacheUUID}")
    public Flux<SubjectTaught> getSubjectsTaughtByTeacher(@PathVariable("teacheUUID") UUID teacherUUID) {
        return subjectTaughtService.getSubjectsTaughtByTeacher(teacherUUID);
    }

    @GetMapping("/subject/{subjectUUID}")
    public Flux<SubjectTaught> getSubjectsTaughtBySubject(@PathVariable("subjectUUID") UUID subjectUUID) {
        return subjectTaughtService.getSubjectsTaughtBySubject(subjectUUID);
    }

    @DeleteMapping("/{uuid}")
    public Mono<Void> deleteSubjectTaught(@PathVariable("uuid") UUID uuid) {
        return subjectTaughtService.deleteSubjectTaught(uuid);
    }

    @DeleteMapping("/teacher/{teacherUUID}")
    public Mono<Void> deleteSubjectsTaughtByTeacher(@PathVariable("teacherUUID") UUID teacherUUID) {
        return subjectTaughtService.deleteSubjectsTaughtByTeacher(teacherUUID);
    }
}
