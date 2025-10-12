package com.edziennikarze.gradebook.grade;

import com.edziennikarze.gradebook.grade.dto.Grade;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.List;

@Repository
public interface GradeRepository extends ReactiveCrudRepository<Grade, UUID> {

    Flux<Grade> findAllByStudentIdAndSubjectId(@NotNull UUID studentId, @NotNull UUID subjectId);

    Flux<Grade> findAllByStudentIdInAndSubjectId(@NotNull List<UUID> studentsIds, @NotNull UUID studentId);

    Flux<Grade> findAllByStudentId(@NotNull UUID studentId);
}
