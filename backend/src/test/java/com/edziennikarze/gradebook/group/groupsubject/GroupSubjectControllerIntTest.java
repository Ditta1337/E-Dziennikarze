package com.edziennikarze.gradebook.group.groupsubject;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroup;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildSubject;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroupSubject;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
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
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.dto.UserResponse;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class GroupSubjectControllerIntTest {

    @Autowired
    private GroupSubjectController groupSubjectController;

    @Autowired
    private GroupSubjectRepository groupSubjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupTestDatabaseCleaner groupTestDatabaseCleaner;

    private List<User> teachers;

    private List<Group> groups;

    private List<Subject> subjects;

    private List<GroupSubject> groupsToSaveSubject;

    @BeforeEach
    void setUp() {
        setUpTeachers();
        setUpGroups();
        setUpSubjects();
        setUpTeacherGroups();
    }

    @AfterEach
    void tearDown() {
        groupTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateTeacherGroup() {
        // when
        GroupSubject savedGroupSubject = groupSubjectController.createTeacherGroup(Mono.just(groupsToSaveSubject.getFirst()))
                .block();

        // then
        assertNotNull(savedGroupSubject);
        assertNotNull(savedGroupSubject.getId());
    }

    @Test
    void shouldGetTeacherGroups() {
        // given
        groupSubjectRepository.saveAll(groupsToSaveSubject)
                .collectList()
                .block();

        UUID jacekId = teachers.getFirst()
                .getId();
        UUID miloszId = teachers.getLast()
                .getId();

        // when
        List<Group> jacekGroups = groupSubjectController.getTeacherGroups(jacekId)
                .collectList()
                .block();

        List<Group> miloszGroups = groupSubjectController.getTeacherGroups(miloszId)
                .collectList()
                .block();

        // then
        assertEquals(2, jacekGroups.size());
        assertEquals(2, miloszGroups.size());
        assertEquals(miloszGroups.getFirst(), groups.getFirst());
    }

    @Test
    void shouldGetGroupTeachers() {
        // given
        groupSubjectRepository.saveAll(groupsToSaveSubject)
                .collectList()
                .block();

        UserResponse jacek = UserResponse.from(teachers.get(1));
        UUID groupAId = groups.get(0)
                .getId();
        UUID groupBId = groups.get(1)
                .getId();

        // when
        List<UserResponse> groupATeachers = groupSubjectController.getGroupTeachers(groupAId)
                .collectList()
                .block();

        List<UserResponse> groupBTeachers = groupSubjectController.getGroupTeachers(groupBId)
                .collectList()
                .block();

        // then
        assertEquals(2, groupATeachers.size());
        assertEquals(2, groupBTeachers.size());
        assertTrue(groupATeachers.contains(jacek));
        assertTrue(groupBTeachers.contains(jacek));
    }

    @Test
    void shouldGetSubjectTeachers() {
        // given
        groupSubjectRepository.saveAll(groupsToSaveSubject)
                .collectList()
                .block();

        UserResponse jacek = UserResponse.from(teachers.get(1));
        UUID firstSubjectId = subjects.getFirst()
                .getId();
        UUID secondSubjectId = subjects.getLast()
                .getId();

        // when
        List<UserResponse> firstSubjectTeachers = groupSubjectController.getSubjectTeachers(firstSubjectId)
                .collectList()
                .block();

        List<UserResponse> secondSubjectTeachers = groupSubjectController.getSubjectTeachers(secondSubjectId)
                .collectList()
                .block();

        // then
        assertEquals(2, firstSubjectTeachers.size());
        assertEquals(1, secondSubjectTeachers.size());
        assertTrue(firstSubjectTeachers.contains(jacek));
        assertTrue(secondSubjectTeachers.contains(jacek));
    }

    private void setUpTeachers() {
        List<User> teacherToSave = List.of(buildUser("jacek@gmail.com", Role.TEACHER, true, true), buildUser("milosz@gmail.com", Role.TEACHER, true, true));

        teachers = userRepository.saveAll(teacherToSave)
                .collectList()
                .block();
    }

    private void setUpSubjects() {
        List<Subject> subjectsToSave = List.of(buildSubject("Matematyka"), buildSubject("Programowanie"));

        subjects = subjectRepository.saveAll(subjectsToSave)
                .collectList()
                .block();
    }

    private void setUpGroups() {
        List<Group> groupsToSave = List.of(buildGroup(1, "A", true), buildGroup(2, "B", true));
        groups = groupRepository.saveAll(groupsToSave)
                .collectList()
                .block();
    }

    private void setUpTeacherGroups() {
        UUID groupAId = groups.get(0)
                .getId();
        UUID groupBId = groups.get(1)
                .getId();
        UUID firstSubjectId = subjects.getFirst()
                .getId();
        UUID secondSubjectId = subjects.getLast()
                .getId();
        UUID firstTeacherId = teachers.getFirst()
                .getId();
        UUID secondTeacherId = teachers.getLast()
                .getId();

        groupsToSaveSubject = List.of(buildGroupSubject(firstTeacherId, groupAId, firstSubjectId, true), buildGroupSubject(firstTeacherId, groupBId, firstSubjectId, true),
                buildGroupSubject(secondTeacherId, groupAId, firstSubjectId, true), buildGroupSubject(secondTeacherId, groupBId, secondSubjectId, true));
    }
}