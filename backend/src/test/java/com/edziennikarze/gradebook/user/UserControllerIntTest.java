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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@ImportTestcontainers(PostgresTestContainerConfig.class)
class UserControllerIntTest {

    private List<User> users;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTestDatabaseCleaner userTestDatabaseCleaner;

    @BeforeEach
    void setup() {
        List<User> usersToAdd = List.of(
                User.builder()
                        .name("Andrzej")
                        .surname("Kowal")
                        .createdAt(LocalDate.now())
                        .address("adres1")
                        .email("123@mail.com")
                        .password("123")
                        .contact("123456789")
                        .imageBase64("abc123")
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build(),
                User.builder()
                        .name("Maciej")
                        .surname("Malinowski")
                        .createdAt(LocalDate.now())
                        .address("adres2")
                        .email("321@onet.pl")
                        .password("xyz")
                        .contact("987654321")
                        .imageBase64("qwerty")
                        .role(Role.STUDENT)
                        .isActive(false)
                        .build()
        );

        users = new ArrayList<>();

        for (User user : usersToAdd) {
            User addedUser = userService.createUser(Mono.just(user)).block();
            users.add(addedUser);
        }
    }

    @AfterEach
    void tearDown() {
        userTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldGetAllUsers() {
        //when
        Flux<User> allUsers = userController.getAllUsers();
        //then
        List<User> allUsersList = allUsers.collectList().block();
        assertEquals(users.get(0), allUsersList.get(0));
    }

    @Test
    void shouldGetUserById() {
        //when
        User user1 = userController.getUser(users.get(0).getId()).block();
        User user2 = userController.getUser(users.get(1).getId()).block();
        //then
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    void updateUser() {
        //when
        User originalUser = userController.getUser(users.get(0).getId()).block();
        User newUser = User.builder()
                .id(originalUser.getId())
                .name("Stanisława")
                .surname("Ger")
                .createdAt(LocalDate.now())
                .address("Czudecka 3")
                .email("Stanislaw.ger@gmail.com")
                .password("Qwe123")
                .contact("605832228")
                .imageBase64("123456")
                .role(Role.STUDENT)
                .isActive(true)
                .build();
        userController.updateUser(Mono.just(newUser)).block();
        //then
        User updatedUser = userController.getUser(newUser.getId()).block();
        assertEquals(newUser, updatedUser);
    }

    @Test
    void shouldActivateUser() {
        //when
        userController.activateUser(users.get(1).getId()).block();
        //then
        assertTrue(userController.getUser(users.get(1).getId()).block().getIsActive());
    }

    @Test
    void shouldDeactivateUser() {
        //when
        userController.deactivateUser(users.get(0).getId()).block();
        //then
        assertFalse(userController.getUser(users.get(0).getId()).block().getIsActive());
    }
}