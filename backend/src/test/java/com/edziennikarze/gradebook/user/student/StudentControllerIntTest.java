package com.edziennikarze.gradebook.user.student;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.guardian.GuardianController;
import com.edziennikarze.gradebook.user.guardian.GuardianRepository;
import com.edziennikarze.gradebook.user.student.dto.StudentsGuardianDTO;
import com.edziennikarze.gradebook.user.utils.TestDatabaseCleaner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@ImportTestcontainers(PostgresTestContainerConfig.class)
class StudentControllerIntTest {

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
    private StudentRepository studentRepository;

    @Autowired
    private StudentController studentController;

    @Autowired
    private GuardianController guardianController;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @AfterEach
    void tearDown() {
        testDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldAddNewStudent() {
        //given
        User user = studentController.createStudent(Mono.just(users.getFirst())).block();

        //when
        Student student = studentRepository.findByUserId(user.getId()).block();

        //then
        Assertions.assertEquals(users.getFirst(), user);
        Assertions.assertEquals(student.getUserId(), user.getId());
    }


    @Test
    void shouldGetAllStudents() {
        //given
        User user1 = studentController.createStudent(Mono.just(users.getFirst())).block();
        User user2 = studentController.createStudent(Mono.just(users.getLast())).block();

        //when
        List<User> allUsers = studentController.getAllStudents().collectList().block();

        //then
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }

    @Test
    void shouldSetStudentsGuardian() {
        //given
        User studentUser = studentController.createStudent(Mono.just(users.getFirst())).block();
        User guardianUserToCreate = User.builder()
                .name("Artur")
                .surname("Dwornik")
                .createdAt(LocalDate.now())
                .address("adres1")
                .email("789@bobo.bo")
                .password("000")
                .contact("192837465")
                .imageBase64("qwertyuiopasdfghjklzxcvbnm")
                .role(Role.GUARDIAN)
                .isActive(false)
                .build();
        User guardian = guardianController.createGuardian(Mono.just(guardianUserToCreate)).block();
        StudentsGuardianDTO studentsGuardianDTO = StudentsGuardianDTO.builder().studentId(studentUser.getId()).guardianId(guardian.getId()).build();

        //when
        studentController.setStudentGuardian(Mono.just(studentsGuardianDTO)).block();
        User studentsGuardian = studentController.getStudentGuardian(studentUser.getId()).block();

        //then
        Assertions.assertEquals(studentsGuardian, guardian);
    }

    @Test
    void shouldActivateAndDeactivateStudentsPreferences() {
        // given
        User studentUser = studentController.createStudent(Mono.just(users.getFirst())).block();
        Student studentEntity = studentService.getStudentByUserId(studentUser.getId()).block();
        UUID studentId = studentEntity.getId();

        // when
        Student studentBeforeActivation = studentService.getStudentByUserId(studentUser.getId()).block();
        Student studentAfterActivation = studentController.activateStudentPreferences(studentId).block();
        Student studentAfterDeactivation = studentController.deactivateStudentPreferences(studentId).block();

        // then
        Assertions.assertFalse(studentBeforeActivation.isCanChoosePreferences());
        Assertions.assertTrue(studentAfterActivation.isCanChoosePreferences());
        Assertions.assertFalse(studentAfterDeactivation.isCanChoosePreferences());
    }
}