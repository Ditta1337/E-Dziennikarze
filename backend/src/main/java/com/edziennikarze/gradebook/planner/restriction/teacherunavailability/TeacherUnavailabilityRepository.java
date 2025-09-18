package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;

@Repository
public interface TeacherUnavailabilityRepository extends ReactiveCrudRepository<TeacherUnavailability, UUID> {

    Flux<TeacherUnavailability> findAllByTeacherId(@NotNull UUID teacherId);
}
