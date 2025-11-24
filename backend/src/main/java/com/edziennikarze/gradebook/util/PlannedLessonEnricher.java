package com.edziennikarze.gradebook.util;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLessonResponse;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.user.dto.User;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class PlannedLessonEnricher {

    public static Mono<Void> setLessonsSubjects(List<PlannedLessonResponse> plannedLessons, List<Subject> subjects) {
        return Mono.fromRunnable(() -> {
            Map<UUID, String> subjectIdToNameMap = createIdToNameMap(subjects, Subject::getId, Subject::getName);
            plannedLessons.forEach(plannedLesson -> plannedLesson.setSubject(subjectIdToNameMap.get(plannedLesson.getSubjectId())));
        });
    }

    public static Mono<Void> setLessonsRooms(List<PlannedLessonResponse> plannedLessons, List<Room> rooms) {
        return Mono.fromRunnable(() -> {
            Map<UUID, String> roomIdToNameMap = createIdToNameMap(rooms, Room::getId, Room::getRoomCode);
            plannedLessons.forEach(plannedLesson ->
                    plannedLesson.setRoom(roomIdToNameMap.get(plannedLesson.getRoomId()))
            );
        });
    }

    public static Mono<Void> setLessonsGroups(List<PlannedLessonResponse> plannedLessons, List<Group> groups) {
        return Mono.fromRunnable(() -> {
            Map<UUID, String> groupIdToNameMap = createIdToNameMap(groups, Group::getId, Group::getGroupCode);
            plannedLessons.forEach(plannedLesson ->
                plannedLesson.setGroup(groupIdToNameMap.get(plannedLesson.getGroupId()))
            );
        });
    }

    public static Mono<Void> setLessonsTeachers(List<PlannedLessonResponse> plannedLessons, List<User> teachers) {
        return Mono.fromRunnable(() -> {
            Map<UUID, String> teacherIdToNameMap = createIdToNameMap(teachers, User::getId, user -> user.getName() + " " + user.getSurname());
            plannedLessons.forEach(plannedLesson ->
                plannedLesson.setTeacher(teacherIdToNameMap.get(plannedLesson.getTeacherId()))
            );
        });
    }

    private static <T> Map<UUID, String> createIdToNameMap(List<T> objects, Function<T, UUID> idGetter, Function<T, String> nameGetter) {
        return objects.stream()
                .collect(Collectors.toMap(idGetter, nameGetter));
    }

}
