package com.edziennikarze.gradebook.property;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.List;

@Repository
public interface PropertyRepository extends ReactiveCrudRepository<Property, UUID> {
    Mono<Property> findByName(@NotNull String name);

    Flux<Property> findAllByNameIn(@NotNull List<String> names);
}
