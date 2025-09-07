package com.edziennikarze.gradebook.lesson.assigned;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildAssignedLesson;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildGroup;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildPlannedLesson;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildRoom;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildSubject;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildUser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;

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

import static org.junit.jupiter.api.Assertions.*;

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

    private List<AssignedLesson> assignedLessons;

    @BeforeEach
    void setUp() {
        User teacherToSave = buildUser("maciek@gmail.com", Role.TEACHER, true, true);
        User teacher = userRepository.save(teacherToSave)
                .block();

        Subject subjectToSave = buildSubject("Matematyka");
        Subject subject = subjectRepository.save(subjectToSave)
                .block();

        Room roomToSave = buildRoom(30, "1");
        Room room = roomRepository.save(roomToSave)
                .block();

        Group groupToSave = buildGroup(1, "1A", true);
        Group group = groupRepository.save(groupToSave)
                .block();

        PlannedLesson mondayPlannedLesson = buildAndSavePlannedLesson(room.getId(), group.getId(), teacher.getId(), subject.getId(), DayOfWeek.MONDAY);
        PlannedLesson cancelledTuesdayPlannedLesson = buildAndSavePlannedLesson(room.getId(), group.getId(), teacher.getId(), subject.getId(),
                DayOfWeek.TUESDAY);
        PlannedLesson modifiedWednesdayPlannedLesson = buildAndSavePlannedLesson(room.getId(), group.getId(), teacher.getId(), subject.getId(),
                DayOfWeek.WEDNESDAY);

        AssignedLesson assignedMondayLesson = buildAssignedLesson(mondayPlannedLesson.getId(), LocalDate.of(2025, 9, 8), false, false);
        AssignedLesson assignedCancelledTuesdayLesson = buildAssignedLesson(cancelledTuesdayPlannedLesson.getId(), LocalDate.of(2025, 9, 9), true, false);
        AssignedLesson assignedModifiedWednesdayLesson = buildAssignedLesson(modifiedWednesdayPlannedLesson.getId(), LocalDate.of(2025, 9, 10), false, true);

        assignedLessons = List.of(assignedMondayLesson, assignedCancelledTuesdayLesson, assignedModifiedWednesdayLesson);
    }

    @AfterEach
    void tearDown() {
        assignedLessonTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateAssignedLesson() {
        // when
        List<AssignedLesson> createdAssignedLessons = assignedLessons.stream()
                .map(assignedLesson -> assignedLessonController.createAssignedLesson(Mono.just(assignedLesson))
                        .block())
                .toList();

        // then
        assertEquals(assignedLessons.size(), createdAssignedLessons.size());
        createdAssignedLessons.forEach(assignedLesson -> assertNotNull(assignedLesson.getId()));
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
        AssignedLesson updatedOriginalAssignedLesson = buildAssignedLesson(originalAssignedLesson.getPlannedLessonId(), originalAssignedLesson.getDate(), true, true);
        updatedOriginalAssignedLesson.setId(originalAssignedLesson.getId());

        // when
        AssignedLesson savedUpdatedAssignedLesson = assignedLessonController.updateAssignedLesson(Mono.just(updatedOriginalAssignedLesson))
                .block();

        // then
        assertEquals(updatedOriginalAssignedLesson, savedUpdatedAssignedLesson);
        assertNotEquals(originalAssignedLesson.isCancelled(), savedUpdatedAssignedLesson.isCancelled());
        assertNotEquals(originalAssignedLesson.isModified(), savedUpdatedAssignedLesson.isModified());
        assertEquals(originalAssignedLesson.getPlannedLessonId(), updatedOriginalAssignedLesson.getPlannedLessonId());
    }

    private PlannedLesson buildAndSavePlannedLesson(UUID roomId, UUID groupId, UUID teacherId, UUID subjectId, DayOfWeek dayOfWeek) {
        PlannedLesson plannedLessonToSave = buildPlannedLesson(roomId, groupId, teacherId, subjectId, dayOfWeek, LocalTime.of(9, 0), LocalTime.of(9, 45), true);

        return plannedLessonRepository.save(plannedLessonToSave)
                .block();
    }
}
