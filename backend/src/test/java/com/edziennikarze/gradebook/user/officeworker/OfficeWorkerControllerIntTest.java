package com.edziennikarze.gradebook.user.officeworker;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.utils.TestDatabaseCleaner;
import org.junit.jupiter.api.*;
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
class OfficeWorkerControllerIntTest {

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
                    .role(Role.STUDENT)
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

    @Autowired
    private OfficeWorkerController officeWorkerController;

    @Autowired
    private OfficeWorkerService officeWorkerService;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @AfterEach
    void tearDown() {
        testDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldAddNewNonPrincipalOfficeWorker() {
        //given
        User user = officeWorkerController.createOfficeWorker(Mono.just(users.getFirst()), false).block();

        //when
        OfficeWorker officeWorker = officeWorkerService.getOfficeWorkerByUserId(user.getId()).block();

        Assertions.assertEquals(users.getFirst(), user);
        Assertions.assertEquals(officeWorker.getUserId(), user.getId());
        Assertions.assertFalse(officeWorker.isPrincipalPriviledge());
    }

    @Test
    void shouldAddNewPrincipalOfficeWorker() {
        //given
        User user = officeWorkerController.createOfficeWorker(Mono.just(users.getFirst()), true).block();

        //when
        OfficeWorker officeWorker = officeWorkerService.getOfficeWorkerByUserId(user.getId()).block();

        Assertions.assertEquals(users.getFirst(), user);
        Assertions.assertEquals(officeWorker.getUserId(), user.getId());
        Assertions.assertTrue(officeWorker.isPrincipalPriviledge());
    }

    @Test
    void shouldGetAllOfficeWorkers() {
        //given
        User user1 = officeWorkerController.createOfficeWorker(Mono.just(users.getFirst()), true).block();
        User user2 = officeWorkerController.createOfficeWorker(Mono.just(users.getLast()), false).block();

        //when
        List<User> allUsers = officeWorkerController.getAllOfficeWorkers().collectList().block();

        //then
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }
}