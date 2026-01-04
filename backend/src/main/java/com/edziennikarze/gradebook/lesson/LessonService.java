package com.edziennikarze.gradebook.lesson;

import java.time.LocalDate;
import java.util.UUID;

import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
import com.edziennikarze.gradebook.lesson.dto.DeleteRequest;
import com.edziennikarze.gradebook.lesson.dto.Lesson;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import com.edziennikarze.gradebook.notification.NotificationService;
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

    private final NotificationService notificationService;

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
                return assignedLessonRepository.save(assignedLesson).flatMap(savedAssignedLesson -> sendNotificationsForCreatedLesson(lesson, savedAssignedLesson));
            });
        });
    }

    public Flux<Lesson> updateLessons(Flux<Lesson> lessons) {
        return lessons.flatMap(lesson ->
                assignedLessonRepository.findById(lesson.getAssignedLessonId())
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
                                                .flatMap(savedAssigned -> sendNotificationsForModifiedLesson(lesson, assignedLesson, savedAssigned, savedPlanned));
                                    });
                        })
        );
    }

    public Mono<Void> deleteLessons(Mono<DeleteRequest> toDelete) {
        return toDelete.flatMapMany(deleteRequest ->
                Flux.fromIterable(deleteRequest.getIds())
                        .flatMap(id -> assignedLessonRepository.findLessonById(id)
                                .flatMap(assignedLesson -> assignedLessonRepository.deleteById(id)
                                        .then(sendNotificationsForDeletedLesson(assignedLesson))
                                )
                        )
        ).then();
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

    private Mono<Lesson> sendNotificationsForModifiedLesson(Lesson lesson, AssignedLesson previousAssignedLesson, AssignedLesson savedAssignedLesson, PlannedLesson savedPlannedLesson) {
        return plannedLessonRepository.findById(lesson.getPlannedLessonId())
                .flatMap(previousPlannedLesson -> {
                    String message = makeModifiedLessonMessage(previousPlannedLesson, previousAssignedLesson, lesson);
                    return studentGroupRepository.findAllByGroupId(lesson.getGroupId())
                            .flatMap(studentGroup ->
                                    notificationService.sendNotification(studentGroup.getStudentId(), message)
                            )
                            .then(notificationService.sendNotification(lesson.getTeacherId(), message).thenReturn(lesson)
                            )
                            .map(l -> {
                                l.setPlannedLessonId(savedPlannedLesson.getId());
                                l.setAssignedLessonId(savedAssignedLesson.getId());
                                return l;
                            });
                });
    }

    private Mono<Lesson> sendNotificationsForCreatedLesson(Lesson lesson, AssignedLesson savedAssignedLesson) {
        String message = makeCreatedLessonMessage(lesson);
        return studentGroupRepository.findAllByGroupId(lesson.getGroupId())
                .flatMap(studentGroup ->
                        notificationService.sendNotification(studentGroup.getStudentId(), message)
                )
                .then(notificationService.sendNotification(lesson.getTeacherId(), message).thenReturn(lesson))
                .map(l -> {
                    l.setPlannedLessonId(savedAssignedLesson.getPlannedLessonId());
                    l.setAssignedLessonId(savedAssignedLesson.getId());
                    return l;
                });
    }

    private Mono<Void> sendNotificationsForDeletedLesson(Lesson lesson) {
        String message = makeDeletedLessonMessage(lesson);
        return studentGroupRepository.findAllByGroupId(lesson.getGroupId())
                .flatMap(studentGroup ->
                        notificationService.sendNotification(studentGroup.getStudentId(), message)
                )
                .then(notificationService.sendNotification(lesson.getTeacherId(), message).then());
    }

    private String makeModifiedLessonMessage(PlannedLesson previousPlannedLesson, AssignedLesson previousAssignedLesson, Lesson modifiedLesson) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Zmodyfikowano lekcję " + previousPlannedLesson.getStartTime() + " - " + previousPlannedLesson.getEndTime() + " w dniu " + previousAssignedLesson.getDate() + " : ");
        if (!previousAssignedLesson.getDate().equals(modifiedLesson.getDate())) {
            stringBuilder.append("Nowa lekcja w dniu: " + modifiedLesson.getDate() + ", ");
        }
        if (!previousPlannedLesson.getStartTime().equals(modifiedLesson.getStartTime()) || !previousPlannedLesson.getEndTime().equals(modifiedLesson.getEndTime())) {
            stringBuilder.append("Nowa lekcja w godzinach: " + modifiedLesson.getStartTime() + " - " + modifiedLesson.getEndTime() + ", ");
        }
        if (!previousPlannedLesson.getSubjectId().equals(UUID.fromString(modifiedLesson.getSubjectId()))) {
            stringBuilder.append("Nowa lekcja o temacie: " + modifiedLesson.getSubject() + ", ");
        }
        if (!previousPlannedLesson.getRoomId().equals(modifiedLesson.getRoomId())) {
            stringBuilder.append("Nowa lekcja w pokoju: " + modifiedLesson.getRoom() + ", ");
        }
        if (!previousPlannedLesson.getGroupId().equals(modifiedLesson.getGroupId())) {
            stringBuilder.append("Nowa lekcja z grupą: " + modifiedLesson.getGroupCode());
        }
        return stringBuilder.toString();
    }

    private String makeCreatedLessonMessage(Lesson lesson) {
        return "Stworzono lekcję " + makeLessonDescription(lesson);
    }

    private String makeDeletedLessonMessage(Lesson lesson) {
        return "Odwołano lekcję " + makeLessonDescription(lesson);
    }

    private String makeLessonDescription(Lesson lesson) {
        return "w godzinach: " +
                lesson.getStartTime() +
                " - " +
                lesson.getEndTime() + " w dniu: " +
                lesson.getDate() +
                ", o temacie: " +
                lesson.getSubject() +
                ", w pokoju: " +
                lesson.getRoom() +
                ", z grupą: " +
                lesson.getGroupCode();
    }
}
