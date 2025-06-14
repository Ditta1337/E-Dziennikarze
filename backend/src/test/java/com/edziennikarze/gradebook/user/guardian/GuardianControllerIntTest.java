package com.edziennikarze.gradebook.user.guardian;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@ImportTestcontainers(PostgresTestContainerConfig.class)
class GuardianControllerIntTest {

    private final List<User> users = List.of(
            User.builder()
                    .name("Andrzej")
                    .surname("Kowal")
                    .createdAt(LocalDate.now())
                    .address("adres1")
                    .email("123@mail.com")
                    .password("123")
                    .contact("123456789")
                    .imageBase64("abc123")
                    .role(Role.GUARDIAN)
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
                    .role(Role.GUARDIAN)
                    .isActive(false)
                    .build()
    );

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianController guardianController;

    @Autowired
    private UserTestDatabaseCleaner userTestDatabaseCleaner;

    @AfterEach
    void tearDown() {
        userTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldAddNewGuardian() {
        //given
        User user = guardianController.createGuardian(Mono.just(users.getFirst())).block();

        //when
        Guardian guardian = guardianRepository.findByUserId(user.getId()).block();

        //then
        Assertions.assertEquals(users.getFirst(), user);
        Assertions.assertEquals(guardian.getUserId(), user.getId());
    }

    @Test
    void shouldGetAllGuardians() {
        //given
        User user1 = guardianController.createGuardian(Mono.just(users.getFirst())).block();
        User user2 = guardianController.createGuardian(Mono.just(users.getFirst())).block();

        //when
        List<User> allUsers = guardianController.getAllGuardians().collectList().block();

        //
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }

}