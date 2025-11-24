package com.edziennikarze.gradebook.subject.subjecttaught;

import com.edziennikarze.gradebook.subject.subjecttaught.dto.SubjectTaught;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@Repository
public interface SubjectTaughtRepository extends ReactiveCrudRepository<SubjectTaught, UUID> {

    Flux<SubjectTaught> findAllByTeacherId(@NotNull UUID teacherId);

    Flux<SubjectTaught> findAllBySubjectId(@NotNull UUID subjectId);

    Mono<Void> deleteAllByTeacherId(@NotNull UUID teacherId);

    Mono<Void> deleteByTeacherIdAndSubjectId(@NotNull UUID teacherId, @NotNull UUID subjectId);
}
