package com.edziennikarze.gradebook.group;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GroupRepository extends ReactiveCrudRepository<Group, UUID> {
    Flux<Group> findAllByIsClass(boolean isClass);

    Flux<Group> findAllByStartYear(int startYear);

    @Modifying
    @Query("UPDATE groups SET start_year = start_year + 1")
    Mono<Integer> incrementAllStartYears();
}
