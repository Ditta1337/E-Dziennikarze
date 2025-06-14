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

    public Mono<Void> deleteSubject(UUID uuid) {
        return subjectRepository.deleteById(uuid);
    }
}
