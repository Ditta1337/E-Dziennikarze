package com.edziennikarze.gradebook.group.studentgroup;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

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
import com.edziennikarze.gradebook.group.util.GroupTestDatabaseCleaner;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.UserResponse;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class StudentGroupControllerIntTest {

    @Autowired
    private StudentGroupController studentGroupController;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupTestDatabaseCleaner groupTestDatabaseCleaner;

    private List<User> students;

    private List<Group> groups;

    private List<StudentGroup> studentGroupsToSave;

    @BeforeEach
    void setUp() {
        setUpStudents();
        setUpGroups();
        setUpStudentGroups();
    }

    @AfterEach
    void tearDown() {
        groupTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateStudentGroup() {
        // when
        StudentGroup savedStudentGroup = studentGroupController.createStudentGroup(Mono.just(studentGroupsToSave.getFirst()))
                .block();

        // then
        assertNotNull(savedStudentGroup);
        assertNotNull(savedStudentGroup.getId());
    }

    @Test
    void shouldGetStudentGroups() {
        // given
        studentGroupRepository.saveAll(studentGroupsToSave)
                .collectList()
                .block();

        UUID arturId = students.get(1)
                .getId();
        UUID maciekId = students.get(0)
                .getId();

        // when
        List<Group> arturGroups = studentGroupController.getStudentGroups(arturId)
                .collectList()
                .block();

        List<Group> maciekGroups = studentGroupController.getStudentGroups(maciekId)
                .collectList()
                .block();

        // then
        assertEquals(2, arturGroups.size());
        assertEquals(1, maciekGroups.size());
        assertEquals(maciekGroups.getFirst(), groups.getFirst());
    }

    @Test
    void shouldGetGroupUsers() {
        // given
        studentGroupRepository.saveAll(studentGroupsToSave)
                .collectList()
                .block();

        UserResponse artur = UserResponse.from(students.get(1));
        UUID groupAId = groups.get(0)
                .getId();
        UUID groupBId = groups.get(1)
                .getId();

        // when
        List<UserResponse> groupAStudents = studentGroupController.getGroupUsers(groupAId)
                .collectList()
                .block();

        List<UserResponse> groupBStudents = studentGroupController.getGroupUsers(groupBId)
                .collectList()
                .block();

        // then
        assertEquals(2, groupAStudents.size());
        assertEquals(2, groupBStudents.size());
        assertTrue(groupAStudents.contains(artur));
        assertTrue(groupBStudents.contains(artur));
    }

    private void setUpStudents() {
        List<User> studentsToSave = List.of(buildUser("maciek@gmail.com", Role.STUDENT, true, true), buildUser("artur@gmail.com", Role.STUDENT, true, true),
                buildUser("szymon@gmail.com", Role.STUDENT, true, true));
        students = userRepository.saveAll(studentsToSave)
                .collectList()
                .block();
    }

    private void setUpGroups() {
        List<Group> groupsToSave = List.of(buildGroup(1, "A", true), buildGroup(2, "B", true));
        groups = groupRepository.saveAll(groupsToSave)
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

        studentGroupsToSave = List.of(buildStudentGroup(arturId, groupAId), buildStudentGroup(arturId, groupBId), buildStudentGroup(maciekId, groupAId),
                buildStudentGroup(szymonId, groupBId));
    }
}