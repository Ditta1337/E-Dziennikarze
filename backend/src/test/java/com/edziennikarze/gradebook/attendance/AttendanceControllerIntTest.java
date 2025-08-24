package com.edziennikarze.gradebook.attendance;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

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

    private List<Attendance> attendances;

    private User student;

    private List<Subject> subjects;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @BeforeEach
    void setUp() {
        User studentToSave = buildUser("artur@gmail.com", Role.GUARDIAN, true, true);
        User teacherToSave = buildUser("maciek@gmail.com", Role.TEACHER, true, true);
        Subject firstSubjectToSave = buildSubject("Matematyka");
        Subject secondSubjectToSave = buildSubject("Angielski");
        Room roomToSave = buildRoom(30, "1");
        Group groupToSave = buildGroup(1, "1A", true);

        student = userRepository.save(studentToSave)
                .block();
        User teacher = userRepository.save(teacherToSave)
                .block();
        Subject firstSubject = subjectRepository.save(firstSubjectToSave)
                .block();
        Subject secondSubject = subjectRepository.save(secondSubjectToSave)
                .block();
        subjects = List.of(firstSubject, secondSubject);
        Room room = roomRepository.save(roomToSave)
                .block();
        Group group = groupRepository.save(groupToSave)
                .block();

        // TODO: change PlannedLesson to AssignedLesson when implemented
        PlannedLesson lessonToSave = PlannedLesson.builder()
                .active(true)
                .roomId(room.getId())
                .groupId(group.getId())
                .teacherId(teacher.getId())
                .subjectId(firstSubject.getId())
                .weekDay(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 45))
                .build();

        PlannedLesson lesson = plannedLessonRepository.save(lessonToSave)
                .block();

        attendances = List.of(buildAttendance(student.getId(), firstSubject.getId(), lesson.getId(), true),
                buildAttendance(student.getId(), firstSubject.getId(), lesson.getId(), true),
                buildAttendance(student.getId(), secondSubject.getId(), lesson.getId(), true),
                buildAttendance(student.getId(), secondSubject.getId(), lesson.getId(), false));
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
        assertEquals(attendances.size(), studentsAttendance.size());
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
        Attendance updatedOriginalAttendance = buildAttendance(student.getId(), originalAttendance.getId(), originalAttendance.getStudentId(), false);
        updatedOriginalAttendance.setStudentId(student.getId());

        // when
        Attendance savedUpdatedAttendance = attendanceController.updateAttendance(Mono.just(updatedOriginalAttendance))
                .block();

        // then
        assertEquals(updatedOriginalAttendance, savedUpdatedAttendance);
        assertNotEquals(originalAttendance.isPresent(), savedUpdatedAttendance.isPresent());
        assertEquals(originalAttendance.getStudentId(), updatedOriginalAttendance.getStudentId());
    }

}
