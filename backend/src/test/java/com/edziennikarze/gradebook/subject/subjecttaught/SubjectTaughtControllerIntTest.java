package com.edziennikarze.gradebook.subject.subjecttaught;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectController;
import com.edziennikarze.gradebook.subject.utils.SubjectTestDatabaseCleaner;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@ImportTestcontainers(PostgresTestContainerConfig.class)
public class SubjectTaughtControllerIntTest {

    @Autowired
    private SubjectController subjectController;


    @Autowired
    private SubjectTaughtController subjectTaughtController;

    @Autowired
    private SubjectTaughtRepository subjectTaughtRepository;

    @Autowired
    private SubjectTestDatabaseCleaner databaseCleaner;


    private List<SubjectTaught> subjectsTaught = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        List<Subject> subjects = List.of(
                buildSubject("Matematyka"),
                buildSubject("Fizyka"),
                buildSubject("J. Polski")
        );
        List<Subject> savedSubjects = subjects.stream()
                .map(subject -> subjectController.createSubject(Mono.just(subject)).block())
                .toList();

//        teacherController.createTeacher(
//                Mono.just(
//                        User.builder()
//                                .name("Maciej")
//                                .surname("Malinowski")
//                                .createdAt(LocalDate.now())
//                                .address("adres2")
//                                .email("321@onet.pl")
//                                .password("xyz")
//                                .contact("987654321")
//                                .imageBase64("qwerty")
//                                .role(Role.TEACHER)
//                                .isActive(false)
//                                .build()
//                )
//        ).block();
//        UUID teacherId = teacherController.getAllTeachers().blockFirst().getId();
//
//        subjectsTaught = List.of(
//                buildSubjectTaught(teacherId, savedSubjects.get(0).getId()),
//                buildSubjectTaught(teacherId, savedSubjects.get(1).getId()),
//                buildSubjectTaught(teacherId, savedSubjects.get(2).getId())
//        );
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanAll();
    }

//    @Test
//    void shouldCreateSubjectTaught() {
//        // when
//        SubjectTaught savedSubjectTaught = subjectTaughtController.createSubjectTaught(Mono.just(subjectsTaught.get(0)))
//                .block();
//
//        // then
//        assertNotNull(savedSubjectTaught);
//        assertNotNull(savedSubjectTaught.getId());
//    }



    private Subject buildSubject(String name) {
        return Subject.builder()
                .name(name)
                .build();
    }

    private SubjectTaught buildSubjectTaught(UUID teacherId, UUID subjectId) {
        return SubjectTaught.builder()
                .teacherId(teacherId)
                .subjectId(subjectId)
                .build();
    }

}
