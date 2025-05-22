package com.edziennikarze.gradebook.user.admin;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.utils.TestDatabaseCleaner;
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
class AdminControllerIntTest {

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
                    .role(Role.ADMIN)
                    .isActive(false)
                    .build()
    );

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminController adminController;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @AfterEach
    void tearDown() {
        testDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldAddNewAdmin() {
        //given
        User user = adminController.createAdmin(Mono.just(users.getFirst())).block();

        //when
        Admin admin = adminService.getAdminByUserId(user.getId()).block();

        //then
        Assertions.assertEquals(users.getFirst(), user);
        Assertions.assertEquals(admin.getUserId(), user.getId());
    }

    @Test
    void shouldGetAllAdmins() {
        //given
        User user1 = adminController.createAdmin(Mono.just(users.getFirst())).block();
        User user2 = adminController.createAdmin(Mono.just(users.getLast())).block();

        //when
        List<User> allUsers = adminController.getAllAdmins().collectList().block();

        //then
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }

}