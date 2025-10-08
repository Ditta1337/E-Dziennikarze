package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailability;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import com.edziennikarze.gradebook.plan.util.PlanTestDatabaseCleaner;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.*;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroup;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class PlanControllerIntTest {

    @Autowired
    private PlanController planController;

    @Autowired
    private PlanTestDatabaseCleaner planTestDatabaseCleaner;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherUnavailabilityRepository teacherUnavailabilityRepository;


    private List<User> students;

    private List<User> teachers;

    private List<Group> groups;

    private List<StudentGroup> studentGroups;

    private List<TeacherUnavailability> teacherUnavailabilities;

    @BeforeEach
    void setUp() {
        setUpStudents();
        setUpTeachers();
        setUpGroups();
        setUpStudentGroups();
        setUpTeacherUnavailability();
    }

    @AfterEach
    void cleanUp() {
        planTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldEnrichPlanCorrectly() {
        // TODO szywoj
        // given

        // when

        // then
    }

    private void setUpStudents() {
        List<User> studentsToSave = List.of(buildUser("maciek@gmail.com", Role.STUDENT, true, true), buildUser("artur@gmail.com", Role.STUDENT, true, true),
                buildUser("szymon@gmail.com", Role.STUDENT, true, true));
        students = userRepository.saveAll(studentsToSave)
                .collectList()
                .block();
    }

    private void setUpTeachers() {
        List<User> teachersToSave = List.of(buildUser("szywoj@gmail.com", Role.TEACHER, true, true), buildUser("maciek@gmail.com", Role.TEACHER, true, true));

        teachers = userRepository.saveAll(teachersToSave)
                .collectList()
                .block();
    }

    private void setUpStudentGroups() {
        UUID maciekId = students.get(0)
                .getId();
        UUID arturId = students.get(1)
                .getId();
        UUID szymonId = students.get(2)
                .getId();
        UUID groupAId = groups.get(0)
                .getId();
        UUID groupBId = groups.get(1)
                .getId();

        List<StudentGroup> studentGroupsToSave = List.of(buildStudentGroup(arturId, groupAId), buildStudentGroup(arturId, groupBId), buildStudentGroup(maciekId, groupAId),
                buildStudentGroup(szymonId, groupBId));
        studentGroups = studentGroupRepository.saveAll(studentGroupsToSave)
                .collectList()
                .block();
    }

    private void setUpGroups() {
        List<Group> groupsToSave = List.of(buildGroup(1, "A", true), buildGroup(2, "B", true));
        groups = groupRepository.saveAll(groupsToSave)
                .collectList()
                .block();
    }

    private void setUpTeacherUnavailability() {
        UUID firstTeacherId = teachers.getFirst()
                .getId();
        UUID secondTeacherId = teachers.getLast()
                .getId();

        teacherUnavailabilities = List.of(buildTeacherUnavailability(firstTeacherId, LocalTime.of(7, 0), LocalTime.of(8, 30), DayOfWeek.MONDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(18, 30), LocalTime.of(20, 15), DayOfWeek.MONDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(8, 0), LocalTime.of(10, 30), DayOfWeek.TUESDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(17, 0), LocalTime.of(21, 30), DayOfWeek.TUESDAY),
                buildTeacherUnavailability(secondTeacherId, LocalTime.of(13, 0), LocalTime.of(17, 30), DayOfWeek.MONDAY));
    }


}
