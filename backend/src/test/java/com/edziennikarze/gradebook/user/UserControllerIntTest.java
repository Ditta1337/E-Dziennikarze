package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildUser;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@ImportTestcontainers(PostgresTestContainerConfig.class)
class UserControllerIntTest {


    @Autowired
    private UserController userController;

    @Autowired
    private UserTestDatabaseCleaner userTestDatabaseCleaner;

    private List<User> users;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        users = List.of(
                buildUser("maciek@gmail.com", Role.ADMIN, false, true),
                buildUser("artur@gmail.com", Role.GUARDIAN, true, true),
                buildUser("szymon@gmail.com", Role.OFFICEWORKER, true, true),
                buildUser("jacek@gmail.com", Role.PRINCIPAL, true, true),
                buildUser("miłosz@gmail.com", Role.TEACHER, false, true),
                buildUser("marcin@gmail.com", Role.STUDENT, true, true),
                buildUser("michał@gmail.com", Role.STUDENT, false, false)
        );
    }

    @AfterEach
    void tearDown() {
        userTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateUsers() {
        // when
        List<User> createdUsers = users.stream()
                .map(user -> userController.createUser(Mono.just(user)).block())
                .toList();

        // then
        assertEquals(users.size(), createdUsers.size());
        createdUsers.forEach(
                user -> assertNotNull(user.getId())
        );
    }

    @Test
    void shouldGetAllUsers() {
        // given
        List<User> savedUsers = userRepository.saveAll(users)
                .collectList()
                .block();

        // when
        List<User> allUsers = userController.getAllUsers(null)
                .collectList()
                .block();

        // then
        assertEquals(users.size(), allUsers.size());
        assertEquals(savedUsers, allUsers);
    }

    @Test
    void shouldGetAllUsersByRole() {
        // given
        List<User> savedUsers = userRepository.saveAll(users)
                .collectList()
                .block();

        // when
        List<User> allStudents = userController.getAllUsers(Role.STUDENT)
                .collectList()
                .block();

        List<User> allAdmins = userController.getAllUsers(Role.ADMIN)
                .collectList()
                .block();

        List<User> allTeachers = userController.getAllUsers(Role.TEACHER)
                .collectList()
                .block();

        List<User> allGuardians = userController.getAllUsers(Role.GUARDIAN)
                .collectList()
                .block();

        List<User> allOfficeWorkers = userController.getAllUsers(Role.OFFICEWORKER)
                .collectList()
                .block();

        List<User> allPrincipals = userController.getAllUsers(Role.PRINCIPAL)
                .collectList()
                .block();

        // then
        assertEquals(2, allStudents.size());
        assertEquals(1, allAdmins.size());
        assertEquals(1, allTeachers.size());
        assertEquals(1, allGuardians.size());
        assertEquals(1, allOfficeWorkers.size());
        assertEquals(1, allPrincipals.size());
    }

    @Test
    void shouldGetUserById() {
        // given
        List<User> savedUsers = userRepository.saveAll(users)
                .collectList()
                .block();

        // when
        User user1 = userController.getUser(savedUsers.getFirst().getId()).block();
        User user2 = userController.getUser(users.getLast().getId()).block();

        // then
        assertEquals(user1, savedUsers.getFirst());
        assertEquals(user2, users.getLast());
    }

    @Test
    void updateUser() {
        // given
        List<User> savedUsers = userRepository.saveAll(users)
                .collectList()
                .block();

        // when
        User originalUser = savedUsers.getFirst();
        User updatedOriginalUser = buildUser("updated_" + originalUser.getEmail(), Role.STUDENT, true, false);
        updatedOriginalUser.setId(originalUser.getId());
        User savedUpdatedUser = userController.updateUser(Mono.just(updatedOriginalUser)).block();

        // then
        assertEquals(updatedOriginalUser, savedUpdatedUser);
        assertNotEquals(originalUser.getRole(), savedUpdatedUser.getRole());
        assertEquals(Role.STUDENT, savedUpdatedUser.getRole());
    }

    @Test
    void shouldActivateUser() {
        // given
        List<User> savedUsers = userRepository.saveAll(users)
                .collectList()
                .block();

        // when
        userController.activateUser(savedUsers.getFirst().getId()).block();

        // then
        assertTrue(userController.getUser(savedUsers.getFirst().getId()).block().isActive());
    }

    @Test
    void shouldDeactivateUser() {
        // given
        List<User> savedUsers = userRepository.saveAll(users)
                .collectList()
                .block();

        // when
        userController.deactivateUser(savedUsers.getFirst().getId()).block();

        // then
        assertFalse(userController.getUser(savedUsers.getFirst().getId()).block().isActive());
    }
}