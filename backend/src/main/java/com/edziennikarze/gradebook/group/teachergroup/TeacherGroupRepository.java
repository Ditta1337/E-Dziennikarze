package com.edziennikarze.gradebook.group.teachergroup;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeacherGroupRepository extends ReactiveCrudRepository<TeacherGroup, UUID> {
    Flux<TeacherGroup> findAllByTeacherId(@NotNull UUID teacherId);

    Flux<TeacherGroup> findAllByGroupId(@NotNull UUID groupId);

    Flux<TeacherGroup> findAllBySubjectId(@NotNull UUID subjectId);

    Mono<Void> deleteAllByTeacherId(@NotNull UUID teacherId);
}
