package com.edziennikarze.gradebook.attendance;

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

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildAttendance;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildGroup;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildRoom;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildSubject;
import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildUser;

import com.edziennikarze.gradebook.attendance.utils.AttendanceTestDatabaseCleaner;
import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
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
class AttendanceControllerIntTest {

    @Autowired
    private AttendanceController attendanceController;

    @Autowired
    private AttendanceTestDatabaseCleaner attendanceTestDatabaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PlannedLessonRepository plannedLessonRepository;

    @Autowired
    private AssignedLessonRepository assignedLessonRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private List<Attendance> attendances;

    private User student;

    private List<Subject> subjects;

    @BeforeEach
    void setUp() {
        User studentToSave = buildUser("artur@gmail.com", Role.GUARDIAN, true, true);
        student = userRepository.save(studentToSave)
                .block();

        User teacherToSave = buildUser("maciek@gmail.com", Role.TEACHER, true, true);
        User teacher = userRepository.save(teacherToSave)
                .block();

        Subject firstSubjectToSave = buildSubject("Matematyka");
        Subject firstSubject = subjectRepository.save(firstSubjectToSave)
                .block();

        Subject secondSubjectToSave = buildSubject("Angielski");
        Subject secondSubject = subjectRepository.save(secondSubjectToSave)
                .block();
        subjects = List.of(firstSubject, secondSubject);

        Room roomToSave = buildRoom(30, "1");
        Room room = roomRepository.save(roomToSave)
                .block();

        Group groupToSave = buildGroup(1, "1A", true);
        Group group = groupRepository.save(groupToSave)
                .block();

        PlannedLesson plannedLesson = buildAndSavePlannedLesson(room.getId(), group.getId(),teacher.getId(), firstSubject.getId());

        AssignedLesson assignedLesson = buildAndSaveAssignedLesson(plannedLesson.getId(), LocalDate.of(2025, 9, 6));

        attendances = List.of(buildAttendance(student.getId(), firstSubject.getId(), assignedLesson.getId(), true),
                buildAttendance(student.getId(), firstSubject.getId(), assignedLesson.getId(), true),
                buildAttendance(student.getId(), secondSubject.getId(), assignedLesson.getId(), true),
                buildAttendance(student.getId(), secondSubject.getId(), assignedLesson.getId(), false));
    }

    @AfterEach
    void tearDown() {
        attendanceTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateAttendance() {
        // when
        List<Attendance> createdAttendances = attendances.stream()
                .map(attendance -> attendanceController.createAttendance(Mono.just(attendance))
                        .block())
                .toList();

        // then
        assertEquals(attendances.size(), createdAttendances.size());
        createdAttendances.forEach(attendance -> assertNotNull(attendance.getId()));
    }

    @Test
    void shouldGetStudentsAttendanceBySubject() {
        // given
        attendanceRepository.saveAll(attendances)
                .collectList()
                .block();

        // when
        List<Attendance> studentsAttendance = attendanceController.getStudentsAttendanceBySubject(student.getId(), subjects.getFirst().getId())
                .collectList()
                .block();

        // then
        assertEquals(2, studentsAttendance.size());
    }

    @Test
    void shouldCorrectlyCalculateUsersAttendanceAverage() {
        // given
        attendanceRepository.saveAll(attendances)
                .collectList()
                .block();

        // when
        double average = attendanceController.getStudentsAverageAttendance(student.getId());

        // then
        assertEquals(0.75, average);
    }

    @Test
    void shouldCorrectlyCalculateUsersAttendanceAverageBySubject() {
        // given
        attendanceRepository.saveAll(attendances)
                .collectList()
                .block();

        // when
        double average = attendanceController.getStudentsAverageAttendanceBySubject(student.getId(), subjects.getFirst()
                .getId());

        // then
        assertEquals(1.0, average);
    }

    @Test
    void shouldUpdateAttendance() {
        // given
        List<Attendance> savedAttendances = attendanceRepository.saveAll(attendances)
                .collectList()
                .block();
        Attendance originalAttendance = savedAttendances.getFirst();
        Attendance updatedOriginalAttendance = buildAttendance(student.getId(), originalAttendance.getSubjectId(), originalAttendance.getLessonId(), false);
        updatedOriginalAttendance.setId(originalAttendance.getId());

        // when
        Attendance savedUpdatedAttendance = attendanceController.updateAttendance(Mono.just(updatedOriginalAttendance))
                .block();

        // then
        assertEquals(updatedOriginalAttendance, savedUpdatedAttendance);
        assertNotEquals(originalAttendance.isPresent(), savedUpdatedAttendance.isPresent());
        assertEquals(originalAttendance.getStudentId(), updatedOriginalAttendance.getStudentId());
    }

    private PlannedLesson buildAndSavePlannedLesson(UUID roomId, UUID groupId, UUID teacherId, UUID subjectId) {
        PlannedLesson plannedLessonToSave =  PlannedLesson.builder()
                .active(true)
                .roomId(roomId)
                .groupId(groupId)
                .teacherId(teacherId)
                .subjectId(subjectId)
                .weekDay(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 45))
                .build();

        return plannedLessonRepository.save(plannedLessonToSave)
                .block();
    }

    private AssignedLesson buildAndSaveAssignedLesson(UUID plannedLessonId, LocalDate date) {
        AssignedLesson assignedLessonToSave = AssignedLesson.builder()
                .plannedLessonId(plannedLessonId)
                .date(date)
                .build();

        return assignedLessonRepository.save(assignedLessonToSave)
                .block();
    }
}
