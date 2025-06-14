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

    public Flux<SubjectTaught> getSubjectsTaughtByTeacher(UUID teacherUUID) {
        return subjectTaughtRepository.findByTeacherId(teacherUUID);
    }

    public Flux<SubjectTaught> getSubjectsTaughtBySubject(UUID subjectUUID) {
        return subjectTaughtRepository.findBySubjectId(subjectUUID);
    }

    public Mono<Void> deleteSubjectTaught(UUID uuid) {
        return subjectTaughtRepository.deleteById(uuid);
    }

    public Mono<Void> deleteSubjectsTaughtByTeacher(UUID teacherUUID) {
        return subjectTaughtRepository.findByTeacherId(teacherUUID)
                .map(SubjectTaught::getId)
                .flatMap(subjectTaughtRepository::deleteById)
                .then();
    }
}
