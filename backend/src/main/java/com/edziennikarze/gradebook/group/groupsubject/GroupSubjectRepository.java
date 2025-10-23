package com.edziennikarze.gradebook.group.groupsubject;

import java.util.List;
import java.util.UUID;

import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubjectResponse;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GroupSubjectRepository extends ReactiveCrudRepository<GroupSubject, UUID> {
    Flux<GroupSubject> findAllByTeacherId(@NotNull UUID teacherId);

    Flux<GroupSubject> findAllByGroupId(@NotNull UUID groupId);

    Flux<GroupSubject> findAllByGroupIdIn(@NotNull List<UUID> groupIds);

    Flux<GroupSubject> findAllBySubjectId(@NotNull UUID subjectId);

    Mono<StudentGroup> findByGroupIdAndSubjectId(@NotNull UUID groupId, @NotNull UUID subjectId);

    Mono<Void> deleteAllByTeacherId(@NotNull UUID teacherId);

    @Query("""
            SELECT
                gs.id AS id,
                gs.subject_id AS subject_id,
                s.name AS subject_name,
                gs.group_id AS group_id,
                g.group_code AS group_code,
                COUNT(sg.student_id) AS students_in_group,
                gs.teacher_id AS teacher_id,
                u.name AS teacher_name,
                u.surname AS teacher_surname,
                gs.active AS active
            FROM group_subjects gs
            INNER JOIN subjects s ON s.id = gs.subject_id
            INNER JOIN groups g ON g.id = gs.group_id
            INNER JOIN users u ON u.id = gs.teacher_id
            INNER JOIN student_groups sg ON sg.group_id = g.id
            WHERE gs.active = true
            GROUP BY
                gs.id,
                gs.subject_id,
                s.name,
                gs.group_id,
                g.group_code,
                gs.teacher_id,
                u.name,
                u.surname,
                gs.active
            """)
    Flux<GroupSubjectResponse> findAllByActiveTrue();
}
