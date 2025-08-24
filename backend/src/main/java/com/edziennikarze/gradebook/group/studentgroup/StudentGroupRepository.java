package com.edziennikarze.gradebook.group.studentgroup;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StudentGroupRepository extends ReactiveCrudRepository<StudentGroup, UUID> {
    Flux<StudentGroup> findAllByStudentId(@NotNull UUID studentId);

    Flux<StudentGroup> findAllByGroupId(@NotNull UUID groupId);

    Mono<Void> deleteAllByStudentId(@NotNull UUID studentId);
}
