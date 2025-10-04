package com.edziennikarze.gradebook.property;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Repository
public interface PropertyRepository extends ReactiveCrudRepository<Property, UUID> {
    Mono<Property> findByName(@NotNull String name);
}
