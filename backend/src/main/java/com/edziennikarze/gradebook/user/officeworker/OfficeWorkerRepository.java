package com.edziennikarze.gradebook.user.officeworker;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OfficeWorkerRepository extends ReactiveCrudRepository<OfficeWorker, UUID> {
    Mono<OfficeWorker> findByUserId(UUID id);
}
