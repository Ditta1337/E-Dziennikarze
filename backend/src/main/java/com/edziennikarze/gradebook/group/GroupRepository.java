package com.edziennikarze.gradebook.group;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface GroupRepository extends ReactiveCrudRepository<Group, UUID> {
    Flux<Group> findAllByIsClass(boolean isClass);

    Flux<Group> findAllByStartYear(int startYear);
}
