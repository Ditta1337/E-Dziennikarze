package com.edziennikarze.gradebook.lesson.assigned;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface AssignedLessonRepository extends ReactiveCrudRepository<AssignedLesson, UUID> {

    Flux<AssignedLesson> findAllByCancelled(boolean cancelled);
}
