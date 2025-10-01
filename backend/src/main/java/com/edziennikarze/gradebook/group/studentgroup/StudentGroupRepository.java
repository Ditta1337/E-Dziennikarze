package com.edziennikarze.gradebook.group.studentgroup;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentGroupRepository extends ReactiveCrudRepository<StudentGroup, UUID> {
    Flux<StudentGroup> findAllByStudentId(@NotNull UUID studentId);

    Flux<StudentGroup> findAllByGroupId(@NotNull UUID groupId);

    Mono<Void> deleteAllByStudentId(@NotNull UUID studentId);
}
