package com.edziennikarze.gradebook.lesson.assigned;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.util.AssignedLessonTestDatabaseCleaner;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
class AssignedLessonControllerIntTest {

    @Autowired
    private AssignedLessonController assignedLessonController;

    @Autowired
    private AssignedLessonTestDatabaseCleaner assignedLessonTestDatabaseCleaner;

    @Autowired
    private AssignedLessonRepository assignedLessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlannedLessonRepository plannedLessonRepository;

    private User teacher;

    private Subject subject;

    private Room room;

    private Group group;

    private List<PlannedLesson> plannedLessons;

    private List<AssignedLesson> assignedLessons;

    @BeforeEach
    void setUp() {
        setUpTeacher();
        setUpSubject();
        setUpRoom();
        setUpGroup();
        setUpPlannedLessons();
        setUpAssignedLessons();
    }

    @AfterEach
    void tearDown() {
        assignedLessonTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateAssignedLesson() {
        // when
        List<AssignedLesson> savedAssignedLessons = assignedLessons.stream()
                .map(assignedLesson -> assignedLessonController.createAssignedLesson(Mono.just(assignedLesson))
                        .block())
                .toList();

        // then
        assertEquals(assignedLessons.size(), savedAssignedLessons.size());
        savedAssignedLessons.forEach(assignedLesson -> assertNotNull(assignedLesson.getId()));
    }

    @Test
    void shouldGetAllAssignedLessons() {
        // given
        assignedLessonRepository.saveAll(assignedLessons)
                .collectList()
                .block();

        // when
        List<AssignedLesson> allAssignedLessons = assignedLessonController.getAllAssignedLessons()
                .collectList()
                .block();

        // then
        assertEquals(assignedLessons.size(), allAssignedLessons.size());
    }

    @Test
    void shouldGetAllCancelledAssignedLessons() {
        // given
        assignedLessonRepository.saveAll(assignedLessons)
                .collectList()
                .block();

        // when
        List<AssignedLesson> allCancelledAssignedLessons = assignedLessonController.getAllAssignedLessonsCancelled()
                .collectList()
                .block();

        // then
        assertEquals(1, allCancelledAssignedLessons.size());
    }

    @Test
    void shouldUpdateAssignedLesson() {
        // given
        List<AssignedLesson> savedAssignedLessons = assignedLessonRepository.saveAll(assignedLessons)
                .collectList()
                .block();
        AssignedLesson originalAssignedLesson = savedAssignedLessons.getFirst();
        AssignedLesson updatedOriginalAssignedLesson = buildAssignedLesson(originalAssignedLesson.getPlannedLessonId(), originalAssignedLesson.getDate(), true,
                true);
        updatedOriginalAssignedLesson.setId(originalAssignedLesson.getId());

        // when
        AssignedLesson savedUpdatedAssignedLesson = assignedLessonController.updateAssignedLesson(Mono.just(updatedOriginalAssignedLesson))
                .block();

        // then
        assertEquals(updatedOriginalAssignedLesson, savedUpdatedAssignedLesson);
        assertNotEquals(originalAssignedLesson.isCancelled(), savedUpdatedAssignedLesson.isCancelled());
        assertNotEquals(originalAssignedLesson.isModified(), savedUpdatedAssignedLesson.isModified());
        assertEquals(originalAssignedLesson.getId(), savedUpdatedAssignedLesson.getId());
    }

    private void setUpTeacher() {
        User teacherToSave = buildUser("maciek@gmail.com", Role.TEACHER, true, true);
        teacher = userRepository.save(teacherToSave)
                .block();
    }

    private void setUpSubject() {
        Subject subjectToSave = buildSubject("Matematyka");
        subject = subjectRepository.save(subjectToSave)
                .block();
    }

    private void setUpRoom() {
        Room roomToSave = buildRoom(30, "1");
        room = roomRepository.save(roomToSave)
                .block();
    }

    private void setUpGroup() {
        Group groupToSave = buildGroup(1, "1A", true);
        group = groupRepository.save(groupToSave)
                .block();
    }

    private void setUpPlannedLessons() {
        List<PlannedLesson> lessonsToSave = List.of(
                buildPlannedLesson(room.getId(), group.getId(), teacher.getId(), subject.getId(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), group.getId(), teacher.getId(), subject.getId(), DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), group.getId(), teacher.getId(), subject.getId(), DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true));
        plannedLessons = plannedLessonRepository.saveAll(lessonsToSave)
                .collectList()
                .block();
    }

    private void setUpAssignedLessons() {
        PlannedLesson mondayLesson = plannedLessons.get(0);
        PlannedLesson tuesdayLesson = plannedLessons.get(1);
        PlannedLesson wednesdayLesson = plannedLessons.get(2);

        assignedLessons = List.of(buildAssignedLesson(mondayLesson.getId(), LocalDate.of(2025, 9, 8), false, false),
                buildAssignedLesson(tuesdayLesson.getId(), LocalDate.of(2025, 9, 9), true, false), // Cancelled
                buildAssignedLesson(wednesdayLesson.getId(), LocalDate.of(2025, 9, 10), false, true) // Modified
        );
    }
}