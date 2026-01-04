package com.edziennikarze.gradebook.lesson.assigned;

import java.util.UUID;

import com.edziennikarze.gradebook.lesson.dto.Lesson;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AssignedLessonRepository extends ReactiveCrudRepository<AssignedLesson, UUID> {

    Flux<AssignedLesson> findAllByCancelled(boolean cancelled);

    @Query("""
                SELECT
                    a.id AS assigned_lesson_id,
                    p.id AS planned_lesson_id,
                    p.group_id AS group_id,
                    g.group_code AS group_code,
                    a.date AS date,
                    a.cancelled AS cancelled,
                    a.modified AS modified,
                    s.id AS subject_id,
                    s.name AS subject,
                    p.start_time AS start_time,
                    p.end_time AS end_time,
                    p.week_day AS week_day,
                    r.id AS room_id,
                    r.room_code AS room,
                    p.teacher_id AS teacher_id
                FROM planned_lessons p
                INNER JOIN assigned_lessons a ON a.planned_lesson_id = p.id
                INNER JOIN subjects s ON s.id = p.subject_id
                INNER JOIN rooms r ON r.id = p.room_id
                INNER JOIN groups g ON g.id = p.group_id
                WHERE a.id = :assignedLessonId
            """)
    Mono<Lesson> findLessonById(UUID assignedLessonId);

}
