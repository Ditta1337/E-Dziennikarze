package com.edziennikarze.gradebook.lesson;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LessonService {

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
}
