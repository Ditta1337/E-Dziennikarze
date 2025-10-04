package com.edziennikarze.gradebook.user.studentguardian;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildStudentGuardian;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.edziennikarze.gradebook.user.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class StudentGuardianControllerIntTest {

    @Autowired
    private StudentGuardianController studentGuardianController;

    @Autowired
    private UserTestDatabaseCleaner userTestDatabaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentGuardianRepository studentGuardianRepository;

    @MockitoBean
    private LoggedInUserService loggedInUserService;

    private List<User> users;

    private List<User> guardians;

    private List<StudentGuardian> studentGuardians;

    @BeforeEach
    void setUp() {
        setUpUsers();
        setUpGuardians();
        setUpStudentGuardians();
    }

    @AfterEach
    void tearDown() {
        userTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateUsers() {
        // when
        List<StudentGuardian> createdStudentGuardians = studentGuardians.stream()
                .map(studentGuardian -> studentGuardianController.createStudentGuardian(Mono.just(studentGuardian))
                        .block())
                .toList();

        // then
        assertEquals(studentGuardians.size(), createdStudentGuardians.size());
        createdStudentGuardians.forEach(studentGuardian -> assertNotNull(studentGuardian.getId()));
    }

    @Test
    void shouldGetGuardiansPupils() {
        // given
        studentGuardianRepository.saveAll(studentGuardians)
                .collectList()
                .block();
        UUID firstGuardianId = guardians.get(0)
                .getId();
        UUID secondGuardianId = guardians.get(1)
                .getId();
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        List<UserResponse> firstGuardianPupils = studentGuardianController.getGuardiansStudents(firstGuardianId)
                .collectList()
                .block();
        List<UserResponse> secondGuardianPupils = studentGuardianController.getGuardiansStudents(secondGuardianId)
                .collectList()
                .block();

        // then
        assertEquals(2, firstGuardianPupils.size());
        assertEquals(1, secondGuardianPupils.size());
    }

    @Test
    void shouldGetAllStudentsGuardians() {
        // given
        studentGuardianRepository.saveAll(studentGuardians)
                .collectList()
                .block();
        UUID firstStudentId = users.get(0)
                .getId();
        UUID secondStudentId = users.get(1)
                .getId();
        UUID thirdStudentId = users.get(2)
                .getId();
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        List<UserResponse> firstStudentGuardians = studentGuardianController.getStudentsGuardians(firstStudentId)
                .collectList()
                .block();
        List<UserResponse> secondStudentGuardians = studentGuardianController.getStudentsGuardians(secondStudentId)
                .collectList()
                .block();
        List<UserResponse> thirdStudentGuardians = studentGuardianController.getStudentsGuardians(thirdStudentId)
                .collectList()
                .block();

        // then
        assertEquals(1, firstStudentGuardians.size());
        assertEquals(1, secondStudentGuardians.size());
        assertEquals(1, thirdStudentGuardians.size());
    }

    @Test
    void shouldDeleteByStudentAndGuardian() {
        // given
        studentGuardianRepository.saveAll(studentGuardians)
                .collectList()
                .block();
        UUID firstStudentId = users.get(0)
                .getId();
        UUID firstGuardianId = guardians.get(0)
                .getId();

        // when
        studentGuardianController.deleteStudentGuardian(firstGuardianId, firstStudentId)
                .block();
        List<StudentGuardian> remainingStudentGuardians = studentGuardianRepository.findAll()
                .collectList()
                .block();

        // then
        assertNotEquals(studentGuardians.size(), remainingStudentGuardians.size());
    }


    private void setUpUsers() {
        List<User> usersToSave = List.of(buildUser("maciek@gmail.com", Role.STUDENT, true, true), buildUser("artur@gmail.com", Role.STUDENT, true, true),
                buildUser("milosz@gmail.com", Role.STUDENT, true, true));

        users = userRepository.saveAll(usersToSave)
                .collectList()
                .block();
    }

    private void setUpGuardians() {
        List<User> guardiansToSave = List.of(buildUser("szymon@gamil.com", Role.GUARDIAN, true, true),
                buildUser("marcin@gmail.com", Role.GUARDIAN, true, true));

        guardians = userRepository.saveAll(guardiansToSave)
                .collectList()
                .block();
    }

    private void setUpStudentGuardians() {
        UUID firstStudentId = users.get(0)
                .getId();
        UUID secondStudentId = users.get(1)
                .getId();
        UUID thirdStudentId = users.get(2)
                .getId();
        UUID firstGuardianId = guardians.get(0)
                .getId();
        UUID secondGuardianId = guardians.get(1)
                .getId();

        studentGuardians = List.of(buildStudentGuardian(firstStudentId, firstGuardianId), buildStudentGuardian(secondStudentId, firstGuardianId),
                buildStudentGuardian(thirdStudentId, secondGuardianId));
    }
}