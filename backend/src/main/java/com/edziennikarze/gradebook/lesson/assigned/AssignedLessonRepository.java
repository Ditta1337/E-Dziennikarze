package com.edziennikarze.gradebook.lesson.assigned;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface AssignedLessonRepository extends ReactiveCrudRepository<AssignedLesson, UUID> {
    Flux<AssignedLesson> findAllByDateBetween(@NotNull LocalDate dateFrom, @NotNull LocalDate dateTo);

    Flux<AssignedLesson> findAllByCancelled(boolean cancelled);
}
