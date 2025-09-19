package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.dto.UserResponse;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class UserControllerIntTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserTestDatabaseCleaner userTestDatabaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private LoggedInUserService loggedInUserService;

    private List<User> users;

    @BeforeEach
    void setUp() {
        setUpUsers();
    }

    @AfterEach
    void tearDown() {
        userTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateUsers() {
        // when
        List<UserResponse> createdUsers = users.stream()
                .map(user -> userController.createUser(Mono.just(user))
                        .block())
                .toList();

        // then
        assertEquals(users.size(), createdUsers.size());
        createdUsers.forEach(user -> assertNotNull(user.getId()));
    }

    @Test
    void shouldGetAllUsers() {
        // given
        userRepository.saveAll(users).collectList().block();

        // when
        List<UserResponse> allUsers = userController.getAllUsers(null)
                .collectList()
                .block();

        // then
        assertNotNull(allUsers);
        assertEquals(users.size(), allUsers.size());

        List<User> sortedOriginalUsers = users.stream().sorted(Comparator.comparing(User::getEmail)).toList();
        allUsers.sort(Comparator.comparing(UserResponse::getEmail));

        for (int i = 0; i < sortedOriginalUsers.size(); i++) {
            assertUserAndResponseMatch(sortedOriginalUsers.get(i), allUsers.get(i));
        }
    }

    @Test
    void shouldGetAllUsersByRole() {
        // given
        userRepository.saveAll(users).collectList().block();

        // when
        List<UserResponse> allStudents = userController.getAllUsers(Role.STUDENT).collectList().block();
        List<UserResponse> allAdmins = userController.getAllUsers(Role.ADMIN).collectList().block();
        List<UserResponse> allTeachers = userController.getAllUsers(Role.TEACHER).collectList().block();
        List<UserResponse> allGuardians = userController.getAllUsers(Role.GUARDIAN).collectList().block();
        List<UserResponse> allOfficeWorkers = userController.getAllUsers(Role.OFFICE_WORKER).collectList().block();
        List<UserResponse> allPrincipals = userController.getAllUsers(Role.PRINCIPAL).collectList().block();

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
        List<User> savedUsers = userRepository.saveAll(users).collectList().block();
        User expectedUser = savedUsers.getFirst();

        // when
        UserResponse actualUserResponse = userController.getUser(expectedUser.getId()).block();

        // then
        assertNotNull(actualUserResponse);
        assertUserAndResponseMatch(expectedUser, actualUserResponse);
    }

    @Test
    void shouldUpdateUser() {
        // given
        User originalUser = userRepository.save(users.getFirst()).block();
        User updatedUserEntity = buildUser("updated_" + originalUser.getEmail(), Role.TEACHER, true, false);
        updatedUserEntity.setId(originalUser.getId());
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        UserResponse savedUpdatedUserResponse = userController.updateUser(Mono.just(updatedUserEntity)).block();

        // then
        assertNotNull(savedUpdatedUserResponse);
        assertUserAndResponseMatch(updatedUserEntity, savedUpdatedUserResponse);
        User persistedUser = userRepository.findById(originalUser.getId()).block();
        assertNotNull(persistedUser);
        assertEquals("updated_" + originalUser.getEmail(), persistedUser.getEmail());
        assertEquals(Role.TEACHER, persistedUser.getRole());
    }


    @Test
    void shouldActivateUser() {
        // given
        User userToActivate = users.getFirst();
        User savedUser = userRepository.save(userToActivate).block();
        assertNotNull(savedUser);

        // when
        userController.activateUser(savedUser.getId()).block();

        // then
        UserResponse activatedUser = userController.getUser(savedUser.getId()).block();
        assertNotNull(activatedUser);
        assertTrue(activatedUser.isActive());
    }

    @Test
    void shouldDeactivateUser() {
        // given
        User userToDeactivate = users.get(1);
        User savedUser = userRepository.save(userToDeactivate).block();
        assertNotNull(savedUser);

        // when
        userController.deactivateUser(savedUser.getId()).block();

        // then
        UserResponse deactivatedUser = userController.getUser(savedUser.getId()).block();
        assertNotNull(deactivatedUser);
        assertFalse(deactivatedUser.isActive());
    }

    private void setUpUsers() {
        users = List.of(
                buildUser("maciek@gmail.com", Role.ADMIN, false, true),
                buildUser("artur@gmail.com", Role.GUARDIAN, true, true),
                buildUser("szymon@gmail.com", Role.OFFICE_WORKER, true, true),
                buildUser("jacek@gmail.com", Role.PRINCIPAL, true, true),
                buildUser("miłosz@gmail.com", Role.TEACHER, false, true),
                buildUser("marcin@gmail.com", Role.STUDENT, true, true),
                buildUser("michał@gmail.com", Role.STUDENT, false, false)
        );
    }

    private void assertUserAndResponseMatch(User user, UserResponse userResponse) {
        assertNotNull(user);
        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getName(), userResponse.getName());
        assertEquals(user.getSurname(), userResponse.getSurname());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getAddress(), userResponse.getAddress());
        assertEquals(user.getContact(), userResponse.getContact());
        assertEquals(user.getRole(), userResponse.getRole());
        assertEquals(user.isActive(), userResponse.isActive());
        assertEquals(user.isChoosingPreferences(), userResponse.isChoosingPreferences());
    }
}