package com.edziennikarze.gradebook.lesson.planned;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import com.edziennikarze.gradebook.lesson.Lesson;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface PlannedLessonRepository extends ReactiveCrudRepository<PlannedLesson, UUID> {
    Flux<PlannedLesson> findAllByGroupId(@NotNull UUID groupId);

    Flux<PlannedLesson> findAllBySubjectId(@NotNull UUID subjectId);

    Flux<PlannedLesson> findAllByTeacherId(@NotNull UUID teacherId);

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
                r.room_code AS room,
                p.teacher_id AS teacher_id
            FROM planned_lessons p
            INNER JOIN assigned_lessons a ON a.planned_lesson_id = p.id
            INNER JOIN subjects s ON s.id = p.subject_id
            INNER JOIN rooms r ON r.id = p.room_id
            INNER JOIN groups g ON g.id = p.group_id    
            WHERE a.date BETWEEN :startDate AND :endDate
              AND g.id = :groupId
            """)
    Flux<Lesson> findAllByGroupIdBetweenDates(UUID groupId, LocalDate startDate, LocalDate endDate
    );

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
                r.room_code AS room,
                p.teacher_id AS teacher_id
            FROM planned_lessons p
            INNER JOIN assigned_lessons a ON a.planned_lesson_id = p.id
            INNER JOIN subjects s ON s.id = p.subject_id
            INNER JOIN rooms r ON r.id = p.room_id
            INNER JOIN groups g ON g.id = p.group_id    
            WHERE a.date BETWEEN :startDate AND :endDate
              AND p.teacher_id = :teacherId
            """)
    Flux<Lesson> findAllByTeacherIdBetweenDates(UUID teacherId, LocalDate startDate, LocalDate endDate);
}
