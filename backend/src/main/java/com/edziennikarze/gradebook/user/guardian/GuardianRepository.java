package com.edziennikarze.gradebook.user.guardian;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GuardianRepository extends ReactiveCrudRepository<Guardian, UUID> {
    Mono<Guardian> findByUserId(UUID userId);
}
