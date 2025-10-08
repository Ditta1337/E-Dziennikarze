package com.edziennikarze.gradebook.subject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public Mono<Subject> createSubject(Mono<Subject> subjectMono) {
        return subjectMono.flatMap(subjectRepository::save);
    }

    public Flux<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Mono<Subject> updateSubject(Mono<Subject> subjectMono) {
        return subjectMono.flatMap(subject -> subjectRepository.findById(subject.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("Subject with id " + subject.getId() + " not found")))
                .flatMap(existingSubject -> {
                    existingSubject.setName(subject.getName());
                    existingSubject.setMaxLessonsPerDay(subject.getMaxLessonsPerDay());
                    existingSubject.setLessonsPerWeek(subject.getLessonsPerWeek());
                    return subjectRepository.save(existingSubject);
                }));
    }

    public Mono<Void> deleteSubject(UUID subjectId) {
        return subjectRepository.deleteById(subjectId);
    }
}
