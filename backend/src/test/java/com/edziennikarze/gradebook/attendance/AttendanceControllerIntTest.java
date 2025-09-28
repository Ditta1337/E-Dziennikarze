package com.edziennikarze.gradebook.attendance;

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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildAttendance;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroup;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildRoom;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildSubject;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildUser;
import static com.edziennikarze.gradebook.attendance.AttendanceStatus.*;

import com.edziennikarze.gradebook.attendance.utils.AttendanceTestDatabaseCleaner;
import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
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
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
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

    @MockitoBean
    private LoggedInUserService loggedInUserService;

    private List<Attendance> attendances;

    private User student;

    private User teacher;

    private Room room;

    private Group group;

    private PlannedLesson plannedLesson;

    private AssignedLesson assignedLesson;

    private List<Subject> subjects;

    @BeforeEach
    void setUp() {
        setUpStudent();
        setUpTeacher();
        setUpSubjects();
        setUpRoom();
        setUpGroup();
        setUpPlannedLesson();
        setUpAssignedLesson();
        setUpAttendances();
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
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        List<Attendance> studentsAttendance = attendanceController.getStudentsAttendanceBySubject(student.getId(), subjects.getFirst()
                        .getId())
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
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        double average = attendanceController.getStudentsAverageAttendance(student.getId())
                .block();

        // then
        assertEquals(0.75, average);
    }

    @Test
    void shouldCorrectlyCalculateUsersAttendanceAverageBySubject() {
        // given
        attendanceRepository.saveAll(attendances)
                .collectList()
                .block();
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        double average = attendanceController.getStudentsAverageAttendanceBySubject(student.getId(), subjects.getFirst()
                .getId())
                .block();

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
        Attendance updatedOriginalAttendance = buildAttendance(student.getId(), originalAttendance.getSubjectId(), originalAttendance.getLessonId(), ABSENT);
        updatedOriginalAttendance.setId(originalAttendance.getId());

        // when
        Attendance savedUpdatedAttendance = attendanceController.updateAttendance(Mono.just(updatedOriginalAttendance))
                .block();

        // then
        assertEquals(updatedOriginalAttendance, savedUpdatedAttendance);
        assertNotEquals(originalAttendance.getStatus(), savedUpdatedAttendance.getStatus());
        assertEquals(originalAttendance.getStudentId(), updatedOriginalAttendance.getStudentId());
    }

    private void setUpStudent() {
        User studentToSave = buildUser("artur@gmail.com", Role.GUARDIAN, true, true);
        student = userRepository.save(studentToSave)
                .block();
    }

    private void setUpTeacher() {
        User teacherToSave = buildUser("maciek@gmail.com", Role.TEACHER, true, true);
        teacher = userRepository.save(teacherToSave)
                .block();
    }

    private void setUpSubjects() {
        Subject firstSubjectToSave = buildSubject("Matematyka");
        Subject firstSubject = subjectRepository.save(firstSubjectToSave)
                .block();

        Subject secondSubjectToSave = buildSubject("Angielski");
        Subject secondSubject = subjectRepository.save(secondSubjectToSave)
                .block();
        subjects = List.of(firstSubject, secondSubject);
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

    private void setUpPlannedLesson() {
        PlannedLesson plannedLessonToSave = PlannedLesson.builder()
                .active(true)
                .roomId(room.getId())
                .groupId(group.getId())
                .teacherId(teacher.getId())
                .subjectId(subjects.getFirst()
                        .getId())
                .weekDay(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 45))
                .build();

        plannedLesson = plannedLessonRepository.save(plannedLessonToSave)
                .block();
    }

    private void setUpAssignedLesson() {
        AssignedLesson assignedLessonToSave = AssignedLesson.builder()
                .plannedLessonId(plannedLesson.getId())
                .date(LocalDate.of(2025, 9, 6))
                .build();

        assignedLesson = assignedLessonRepository.save(assignedLessonToSave)
                .block();
    }

    private void setUpAttendances() {
        attendances = List.of(buildAttendance(student.getId(), subjects.getFirst().getId(), assignedLesson.getId(), PRESENT),
                buildAttendance(student.getId(), subjects.getFirst().getId(), assignedLesson.getId(), PRESENT),
                buildAttendance(student.getId(), subjects.getLast().getId(), assignedLesson.getId(), PRESENT),
                buildAttendance(student.getId(), subjects.getLast().getId(), assignedLesson.getId(), ABSENT));
    }
}
