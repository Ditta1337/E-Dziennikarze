package com.edziennikarze.gradebook.lesson;

import java.time.LocalDate;
import java.util.UUID;

import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

@Service
@AllArgsConstructor
public class LessonService {

    private final AssignedLessonRepository assignedLessonRepository;

    private final PlannedLessonRepository plannedLessonRepository;

    private final StudentGroupRepository studentGroupRepository;

    public Flux<Lesson> getAllLessonsByStudentIdBetweenDates(UUID studentId, LocalDate from, LocalDate to) {
        return studentGroupRepository.findAllByStudentId(studentId)
                .map(StudentGroup::getGroupId)
                .flatMap(groupId -> getAllLessonsByGroupIdBetweenDates(groupId, from, to));
    }

    public Flux<Lesson> getAllLessonsByTeacherIdBetweenDates(UUID teacherId, LocalDate from, LocalDate to) {
        return plannedLessonRepository.findAllByTeacherIdBetweenDates(teacherId, from, to);
    }

    private Flux<Lesson> getAllLessonsByGroupIdBetweenDates(UUID groupId, LocalDate from, LocalDate to) {
        return plannedLessonRepository.findAllByGroupIdBetweenDates(groupId, from, to);
    }

    private Mono<Tuple2<AssignedLesson, PlannedLesson>> enrichWithPlannedLesson(AssignedLesson assignedLesson) {
        return Mono.just(assignedLesson)
                .zipWith(plannedLessonRepository.findById(assignedLesson.getPlannedLessonId()));
    }

    private boolean isLessonForGroup(Tuple2<AssignedLesson, PlannedLesson> lessonTuple, UUID groupId) {
        PlannedLesson plannedLesson = lessonTuple.getT2();
        return plannedLesson.getGroupId().equals(groupId);
    }

    private boolean isLessonTaughtByTeacher(Tuple2<AssignedLesson, PlannedLesson> lessonTuple, UUID teacherId) {
        PlannedLesson plannedLesson = lessonTuple.getT2();
        return plannedLesson.getTeacherId().equals(teacherId);
    }
}
