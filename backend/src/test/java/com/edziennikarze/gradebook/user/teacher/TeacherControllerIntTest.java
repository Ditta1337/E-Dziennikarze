package com.edziennikarze.gradebook.user.teacher;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;
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
class TeacherControllerIntTest {

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
                    .role(Role.TEACHER)
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
                    .role(Role.TEACHER)
                    .isActive(false)
                    .build()
    );

    @Autowired
    private TeacherController teacherController;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserTestDatabaseCleaner userTestDatabaseCleaner;

    @AfterEach
    void tearDown() {
        userTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldAddNewTeacher() {
        //given
        User user = teacherController.createTeacher(Mono.just(users.getFirst())).block();

        //when
        Teacher teacher = teacherService.getTeacherByUserId(user.getId()).block();

        //then
        Assertions.assertEquals(users.getFirst(), user);
        Assertions.assertEquals(teacher.getUserId(), user.getId());
    }

    @Test
    void shouldGetAllTeachers() {
        //given
        User user1 = teacherController.createTeacher(Mono.just(users.getFirst())).block();
        User user2 = teacherController.createTeacher(Mono.just(users.getLast())).block();

        //when
        List<User> allUsers = teacherController.getAllTeachers().collectList().block();

        //then
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }
}