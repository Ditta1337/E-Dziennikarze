package com.edziennikarze.gradebook.user.teacher;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TeacherRepository extends ReactiveCrudRepository<Teacher, UUID> {
    Mono<Teacher> findByUserId(UUID userId);
}
