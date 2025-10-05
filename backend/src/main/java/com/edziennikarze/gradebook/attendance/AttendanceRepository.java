package com.edziennikarze.gradebook.attendance;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AttendanceRepository extends ReactiveCrudRepository<Attendance, UUID> {

    Flux<Attendance> findAllByStudentId(@NotNull UUID studentId);

    Flux<Attendance> findAllByStudentIdAndSubjectId(@NotNull UUID studentId, @NotNull UUID subjectId);

    Flux<Attendance> findAllByLessonId(@NotNull UUID lessonId);

    Mono<Void> deleteByStudentId(@NotNull UUID studentId);
}
