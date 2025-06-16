package com.edziennikarze.gradebook.subject.subjecttaught;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SubjectTaughtRepository extends ReactiveCrudRepository<SubjectTaught, UUID> {

    Flux<SubjectTaught> findBySubjectId(UUID subjectId);

    Flux<SubjectTaught> findByTeacherId(UUID teacherId);

    Mono<Void> deleteAllByTeacherId(UUID teacherId);
}
