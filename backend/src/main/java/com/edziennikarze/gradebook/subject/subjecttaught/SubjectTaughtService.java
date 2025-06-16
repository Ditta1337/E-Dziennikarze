package com.edziennikarze.gradebook.subject.subjecttaught;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectTaughtService {

    private final SubjectTaughtRepository subjectTaughtRepository;

    public Mono<SubjectTaught> createSubjectTaught(Mono<SubjectTaught> subjectTaughtMono) {
        return subjectTaughtMono.flatMap(subjectTaughtRepository::save);
    }

    public Flux<SubjectTaught> createSubjectsTaught(Flux<SubjectTaught> subjectTaughtFlux) {
        return subjectTaughtRepository.saveAll(subjectTaughtFlux);
    }

    public Flux<SubjectTaught> getAllSubjectsTaught() {
        return subjectTaughtRepository.findAll();
    }

    public Flux<SubjectTaught> getSubjectsTaughtByTeacher(UUID teacherId) {
        return subjectTaughtRepository.findByTeacherId(teacherId);
    }

    public Flux<SubjectTaught> getSubjectsTaughtBySubject(UUID subjectId) {
        return subjectTaughtRepository.findBySubjectId(subjectId);
    }

    public Mono<Void> deleteSubjectTaught(UUID subjectTaughtId) {
        return subjectTaughtRepository.deleteById(subjectTaughtId);
    }

    public Mono<Void> deleteSubjectsTaughtByTeacher(UUID teacherId) {
        return subjectTaughtRepository.deleteAllByTeacherId(teacherId);
    }
}
