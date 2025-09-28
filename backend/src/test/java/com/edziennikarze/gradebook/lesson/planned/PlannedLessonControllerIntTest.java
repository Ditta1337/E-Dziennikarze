package com.edziennikarze.gradebook.lesson.planned;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroup;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildPlannedLesson;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildRoom;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildSubject;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildUser;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.planned.util.PlannedLessonTestDatabaseCleaner;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class PlannedLessonControllerIntTest {

    @Autowired
    private PlannedLessonController plannedLessonController;

    @Autowired
    private PlannedLessonTestDatabaseCleaner plannedLessonTestDatabaseCleaner;

    @Autowired
    private PlannedLessonRepository plannedLessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoomRepository roomRepository;

    private List<User> teachers;

    private List<Subject> subjects;

    private List<Group> groups;

    private List<PlannedLesson> plannedLessons;

    private Room room;

    @BeforeEach
    void setUp() {
        setUpTeachers();
        setUpSubjects();
        setUpGroups();
        setUpRoom();
        setUpPlannedLessons();
    }

    @AfterEach
    void tearDown() {
        plannedLessonTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreatePlannedLessons() {
        // when
        List<PlannedLesson> savedPlannedLessons = plannedLessons.stream()
                .map(plannedLesson -> plannedLessonController.createPlannedLesson(Mono.just(plannedLesson))
                        .block())
                .toList();

        // then
        assertEquals(plannedLessons.size(), savedPlannedLessons.size());
        savedPlannedLessons.forEach(plannedLesson -> assertNotNull(plannedLesson.getId()));
    }

    @Test
    void shouldGetAllPlannedLessons() {
        // given
        plannedLessonRepository.saveAll(plannedLessons)
                .collectList()
                .block();

        // when
        List<PlannedLesson> allPlannedLessons = plannedLessonController.getAllPlannedLessons()
                .collectList()
                .block();

        // then
        assertEquals(plannedLessons.size(), allPlannedLessons.size());
    }

    @Test
    void shouldGetAllPlannedLessonsByTeacher() {
        // given
        plannedLessonRepository.saveAll(plannedLessons)
                .collectList()
                .block();
        UUID firstTeacherId = teachers.getFirst()
                .getId();

        // when
        List<PlannedLesson> allPlannedLessons = plannedLessonController.getAllPlannedLessonByTeacher(firstTeacherId)
                .collectList()
                .block();

        // then
        assertEquals(2, allPlannedLessons.size());
    }

    @Test
    void shouldGetAllPlannedLessonsByGroup() {
        // given
        plannedLessonRepository.saveAll(plannedLessons)
                .collectList()
                .block();
        UUID firstGroupId = groups.getFirst()
                .getId();

        // when
        List<PlannedLesson> allPlannedLessons = plannedLessonController.getAllPlannedLessonByGroup(firstGroupId)
                .collectList()
                .block();

        // then
        assertEquals(1, allPlannedLessons.size());
    }

    @Test
    void shouldGetAllPlannedLessonsBySubject() {
        // given
        plannedLessonRepository.saveAll(plannedLessons)
                .collectList()
                .block();
        UUID firstSubjectId = subjects.getFirst()
                .getId();

        // when
        List<PlannedLesson> allPlannedLessons = plannedLessonController.getAllPlannedLessonBySubject(firstSubjectId)
                .collectList()
                .block();

        // then
        assertEquals(1, allPlannedLessons.size());
    }

    @Test
    void shouldUpdatePlannedLesson() {
        // given
        List<PlannedLesson> savedPlannedLessons = plannedLessonRepository.saveAll(plannedLessons)
                .collectList()
                .block();
        PlannedLesson originalPlannedLesson = savedPlannedLessons.getFirst();
        PlannedLesson updatedOriginalPlannedLesson = getCopyOfPlannedLesson(originalPlannedLesson);
        updatedOriginalPlannedLesson.setGroupId(groups.getLast().getId());

        // when
        PlannedLesson savedUpdatedPlannedLesson = plannedLessonController.updatePlannedLesson(Mono.just(updatedOriginalPlannedLesson))
                .block();

        // then
        assertEquals(updatedOriginalPlannedLesson, savedUpdatedPlannedLesson);
        assertNotEquals(originalPlannedLesson.getGroupId(), savedUpdatedPlannedLesson.getGroupId());
        assertEquals(originalPlannedLesson.getId(), savedUpdatedPlannedLesson.getId());
    }

    private void setUpTeachers() {
        User firstTeacherToSave = buildUser("maciek@gmail.com", Role.TEACHER, true, true);
        User firstTeacher = userRepository.save(firstTeacherToSave)
                .block();
        User secondTeacherToSave = buildUser("artur@gmail.com", Role.TEACHER, true, true);
        User secondTeacher = userRepository.save(secondTeacherToSave)
                .block();
        teachers = List.of(firstTeacher, secondTeacher);
    }

    private void setUpSubjects() {
        Subject firstSubjectToSave = buildSubject("Matematyka");
        Subject firstSubject = subjectRepository.save(firstSubjectToSave)
                .block();
        Subject secondSubjectToSave = buildSubject("Informatyka");
        Subject secondSubject = subjectRepository.save(secondSubjectToSave)
                .block();
        subjects = List.of(firstSubject, secondSubject);
    }

    private void setUpRoom() {
        Room roomToSave = buildRoom(30, "1");
        room = roomRepository.save(roomToSave)
                .block();
    }

    private void setUpGroups() {
        Group firstGroupToSave = buildGroup(1, "1A", true);
        Group firstGroup = groupRepository.save(firstGroupToSave)
                .block();
        Group secondGroupToSave = buildGroup(2, "2B", true);
        Group secondGroup = groupRepository.save(secondGroupToSave)
                .block();
        groups = List.of(firstGroup, secondGroup);
    }

    private void setUpPlannedLessons() {
        PlannedLesson mondayPlannedLesson = buildPlannedLesson(room.getId(), groups.getFirst()
                .getId(), teachers.getFirst()
                .getId(), subjects.getFirst()
                .getId(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(8, 45), true);
        PlannedLesson tuesdayPlannedLesson = buildPlannedLesson(room.getId(), groups.getLast()
                .getId(), teachers.getFirst()
                .getId(), subjects.getLast()
                .getId(), DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(8, 45), true);
        PlannedLesson wednesdayPlannedLesson = buildPlannedLesson(room.getId(), groups.getLast()
                .getId(), teachers.getLast()
                .getId(), subjects.getLast()
                .getId(), DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(8, 45), true);
        plannedLessons = List.of(mondayPlannedLesson, tuesdayPlannedLesson, wednesdayPlannedLesson);
    }

    private PlannedLesson getCopyOfPlannedLesson(PlannedLesson originalPlannedLesson) {
        return PlannedLesson.builder()
                .id(originalPlannedLesson.getId())
                .roomId(originalPlannedLesson.getRoomId())
                .groupId(originalPlannedLesson.getGroupId())
                .teacherId(originalPlannedLesson.getTeacherId())
                .subjectId(originalPlannedLesson.getSubjectId())
                .weekDay(originalPlannedLesson.getWeekDay())
                .startTime(originalPlannedLesson.getStartTime())
                .endTime(originalPlannedLesson.getEndTime())
                .active(originalPlannedLesson.isActive())
                .build();
    }
}