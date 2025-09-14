package com.edziennikarze.gradebook.lesson;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
import com.edziennikarze.gradebook.lesson.assigned.util.AssignedLessonTestDatabaseCleaner;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.UserRepository;

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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class LessonControllerIntTest {

    @Autowired
    private LessonController lessonController;

    @Autowired
    private AssignedLessonTestDatabaseCleaner assignedLessonTestDatabaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AssignedLessonRepository assignedLessonRepository;

    @Autowired
    private PlannedLessonRepository plannedLessonRepository;

    private List<User> students;

    private User teacher;

    private Subject subject;

    private Room room;

    private List<Group> groups;

    private List<PlannedLesson> plannedLessons;

    private List<AssignedLesson> assignedLessons;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @BeforeEach
    void setUp() {
        setUpStudent();
        setUpTeacher();
        setUpSubject();
        setUpRoom();
        setUpGroupAndAssignStudent();
        setUpPlannedLessons();
        setUpAssignedLessons();
    }

    @AfterEach
    void tearDown() {
        assignedLessonTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldGetAllStudentsLessonsBetweenDates() {
        // given
        assignedLessonRepository.saveAll(assignedLessons)
                .collectList()
                .block();
        UUID firstStudentId = students.getFirst()
                .getId();
        UUID firstGroupId = groups.getFirst()
                .getId();

        // when
        List<Lesson> fetchedLessons = lessonController.getAllStudentLessonsBetweenDates(firstStudentId, LocalDate.of(2025, 9, 9), LocalDate.of(2025, 9, 11))
                .collectList()
                .block();

        // then
        assertEquals(3, fetchedLessons.size());
        assertTrue(fetchedLessons.stream()
                .allMatch(lesson -> lesson.getPlannedLesson()
                        .getGroupId()
                        .equals(firstGroupId)));
    }

    @Test
    void shouldGetAllTeachersLessonsBetweenDates() {
        // given
        assignedLessonRepository.saveAll(assignedLessons)
                .collectList()
                .block();
        UUID teacherId = teacher.getId();

        // when
        List<Lesson> fetchedLessons = lessonController.getAllTeacherLessonsBetweenDates(teacherId, LocalDate.of(2025, 9, 9), LocalDate.of(2025, 9, 11))
                .collectList()
                .block();

        // then
        assertEquals(5, fetchedLessons.size());
        assertTrue(fetchedLessons.stream()
                .allMatch(lesson -> lesson.getPlannedLesson()
                        .getTeacherId()
                        .equals(teacherId)));
    }

    private void setUpStudent() {
        User firstStudentToSave = buildUser("szymon@gmail.com", Role.STUDENT, true, true);
        User secondStudentToSave = buildUser("jasiek@gmail.com", Role.STUDENT, true, true);
        students = userRepository.saveAll(List.of(firstStudentToSave, secondStudentToSave))
                .collectList()
                .block();
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

    private void setUpGroupAndAssignStudent() {
        Group firstGroupToSave = buildGroup(1, "1A", true);
        Group firstGroup = groupRepository.save(firstGroupToSave)
                .block();
        Group secondGroupToSave = buildGroup(1, "1B", true);
        Group secondGroup = groupRepository.save(secondGroupToSave)
                .block();
        groups = List.of(firstGroup, secondGroup);
        StudentGroup firstStudentGroupToSave = buildStudentGroup(students.getFirst()
                .getId(), firstGroup.getId());
        StudentGroup secondStudentGroupToSave = buildStudentGroup(students.getLast()
                .getId(), secondGroup.getId());
        studentGroupRepository.saveAll(List.of(firstStudentGroupToSave, secondStudentGroupToSave))
                .collectList()
                .block();
    }

    private void setUpPlannedLessons() {
        UUID firstGroupId = groups.getFirst()
                .getId();
        UUID secondGroupId = groups.getLast()
                .getId();
        List<PlannedLesson> lessonsToSave = List.of(
                buildPlannedLesson(room.getId(), firstGroupId, teacher.getId(), subject.getId(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), firstGroupId, teacher.getId(), subject.getId(), DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), firstGroupId, teacher.getId(), subject.getId(), DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), firstGroupId, teacher.getId(), subject.getId(), DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), firstGroupId, teacher.getId(), subject.getId(), DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(9, 45),
                        true),
                buildPlannedLesson(room.getId(), secondGroupId, teacher.getId(), subject.getId(), DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(10, 45),
                        true), buildPlannedLesson(room.getId(), secondGroupId, teacher.getId(), subject.getId(), DayOfWeek.WEDNESDAY, LocalTime.of(10, 0),
                        LocalTime.of(10, 45), true));
        plannedLessons = plannedLessonRepository.saveAll(lessonsToSave)
                .collectList()
                .block();
    }

    private void setUpAssignedLessons() {
        PlannedLesson mondayLessonGrA = plannedLessons.get(0);
        PlannedLesson tuesdayLessonGrA = plannedLessons.get(1);
        PlannedLesson wednesdayLessonGrA = plannedLessons.get(2);
        PlannedLesson thursdayLessonGrA = plannedLessons.get(3);
        PlannedLesson fridayLessonGrA = plannedLessons.get(4);
        PlannedLesson tuesdayLessonGrB = plannedLessons.get(5);
        PlannedLesson wednesdayLessonGrB = plannedLessons.get(6);

        assignedLessons = List.of(buildAssignedLesson(mondayLessonGrA.getId(), LocalDate.of(2025, 8, 7), false, false),
                buildAssignedLesson(tuesdayLessonGrA.getId(), LocalDate.of(2025, 9, 9), false, false),
                buildAssignedLesson(wednesdayLessonGrA.getId(), LocalDate.of(2025, 9, 10), false, false),
                buildAssignedLesson(thursdayLessonGrA.getId(), LocalDate.of(2025, 9, 11), false, false),
                buildAssignedLesson(fridayLessonGrA.getId(), LocalDate.of(2025, 9, 12), false, false),
                buildAssignedLesson(tuesdayLessonGrB.getId(), LocalDate.of(2025, 9, 9), false, false),
                buildAssignedLesson(wednesdayLessonGrB.getId(), LocalDate.of(2025, 9, 10), false, false));
    }
}