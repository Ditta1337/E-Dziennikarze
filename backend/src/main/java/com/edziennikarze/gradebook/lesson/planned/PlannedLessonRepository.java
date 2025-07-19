package com.edziennikarze.gradebook.lesson.planned;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

public interface PlannedLessonRepository extends ReactiveCrudRepository<PlannedLesson, UUID> {
    Flux<PlannedLesson> findAllByGroupId(@NotNull UUID groupId);

    Flux<PlannedLesson> findAllBySubjectId(@NotNull UUID subjectId);

    Flux<PlannedLesson> findAllByTeacherId(@NotNull UUID teacherId);
}
