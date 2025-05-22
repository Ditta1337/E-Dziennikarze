package com.edziennikarze.gradebook.user.student;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StudentRepository extends ReactiveCrudRepository<Student, UUID> {
    Mono<Student> findByUserId(UUID id);
}
