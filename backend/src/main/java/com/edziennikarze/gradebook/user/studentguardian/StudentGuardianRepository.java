package com.edziennikarze.gradebook.user.studentguardian;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface StudentGuardianRepository extends ReactiveCrudRepository<StudentGuardian, UUID> {

    Flux<StudentGuardian> findAllByStudentId(@NotNull UUID studentId);

    Flux<StudentGuardian> findAllByGuardianId(@NotNull UUID guardianId);

    Mono<Void> deleteAllByGuardianId(@NotNull UUID guardianId);

    Mono<Void> deleteAllByStudentId(@NotNull UUID studentId);

    Mono<Void> deleteByGuardianIdAndStudentId(@NotNull UUID guardianId, @NotNull UUID studentId);
}
