package com.edziennikarze.gradebook.user.studentguardian;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StudentGuardianRepository extends ReactiveCrudRepository<StudentGuardian, UUID> {

    Flux<StudentGuardian> findAllByStudentId(UUID studentId);

    Flux<StudentGuardian> findAllByGuardianId(UUID guardianId);

    Mono<Void> deleteAllByGuardianId(UUID guardianId);

    Mono<Void> deleteAllByStudentId(UUID studentId);

    Mono<Void> deleteByGuardianAndStudentId(UUID guardianId, UUID studentId);
}
