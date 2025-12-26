package com.edziennikarze.gradebook.lesson;

import java.time.LocalDate;
import java.util.UUID;

import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
import com.edziennikarze.gradebook.lesson.dto.DeleteRequest;
import com.edziennikarze.gradebook.lesson.dto.Lesson;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final PlannedLessonRepository plannedLessonRepository;

    private final AssignedLessonRepository assignedLessonRepository;

    private final StudentGroupRepository studentGroupRepository;

    public Flux<Lesson> getAllLessonsByStudentIdBetweenDates(UUID studentId, LocalDate from, LocalDate to) {
        return studentGroupRepository.findAllByStudentId(studentId)
                .map(StudentGroup::getGroupId)
                .flatMap(groupId -> getAllLessonsByGroupIdBetweenDates(groupId, from, to));
    }

    public Flux<Lesson> getAllLessonsByTeacherIdBetweenDates(UUID teacherId, LocalDate from, LocalDate to) {
        return plannedLessonRepository.findAllByTeacherIdBetweenDates(teacherId, from, to);
    }

    public Flux<Lesson> getAllLessonsByGroupIdBetweenDates(UUID groupId, LocalDate from, LocalDate to) {
        return plannedLessonRepository.findAllByGroupIdBetweenDates(groupId, from, to);
    }

    public Flux<Lesson> createLessons(Flux<Lesson> lessonFlux) {
        return lessonFlux.flatMap(lesson -> {
            PlannedLesson plannedLesson = makePlannedLessonFromLesson(lesson);
            return plannedLessonRepository.save(plannedLesson).flatMap(plannedLessonSaved -> {
                AssignedLesson assignedLesson = AssignedLesson.builder()
                        .plannedLessonId(plannedLessonSaved.getId())
                        .date(lesson.getDate())
                        .build();
                return assignedLessonRepository.save(assignedLesson).flatMap(assignedLessonSaved -> {
                    lesson.setAssignedLessonId(assignedLessonSaved.getId());
                    lesson.setPlannedLessonId(plannedLessonSaved.getId());
                    return Mono.just(lesson);
                });
            });
        });
    }

    public Flux<Lesson> updateLessons(Flux<Lesson> lessons) {
        return lessons.flatMap(lesson -> assignedLessonRepository.findById(lesson.getAssignedLessonId())
                .switchIfEmpty(Mono.error(
                        new IllegalArgumentException(
                                "AssignedLesson not found: " + lesson.getAssignedLessonId()
                        )
                ))
                .flatMap(assignedLesson -> {

                    PlannedLesson plannedLesson = makePlannedLessonFromLesson(lesson);

                    return plannedLessonRepository.save(plannedLesson)
                            .flatMap(savedPlanned -> {
                                assignedLesson.setPlannedLessonId(savedPlanned.getId());
                                assignedLesson.setDate(lesson.getDate());
                                assignedLesson.setModified(true);
                                return assignedLessonRepository.save(assignedLesson)
                                        .map(savedAssigned -> {
                                            lesson.setPlannedLessonId(savedPlanned.getId());
                                            lesson.setAssignedLessonId(savedAssigned.getId());
                                            return lesson;
                                        });
                            });
                })
        );
    }

    public Mono<Void> deleteLessons(Mono<DeleteRequest> toDelete) {
        return toDelete.flatMap(deleteRequest -> assignedLessonRepository.deleteAllById(deleteRequest.getIds()));
    }

    private PlannedLesson makePlannedLessonFromLesson(Lesson lesson) {
        return PlannedLesson.builder()
                .subjectId(UUID.fromString(lesson.getSubjectId()))
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .weekDay(lesson.getWeekDay())
                .roomId(lesson.getRoomId())
                .groupId(lesson.getGroupId())
                .teacherId(lesson.getTeacherId())
                .build();
    }
}
