package com.edziennikarze.gradebook.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Flux<User> findAllByRole(@NotNull Role role);
}
