package com.edziennikarze.gradebook.subject.subjecttaught;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectController;
import com.edziennikarze.gradebook.subject.utils.SubjectTestDatabaseCleaner;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

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
    @Autowired
    private UserController userController;

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

        User savedUser = userController.createUser(
               Mono.just(buildUser("artur@gmail.com", Role.TEACHER, true, true))
        ).block();
        UUID teacherId = savedUser.getId();

        subjectsTaught = List.of(
                buildSubjectTaught(teacherId, savedSubjects.get(0).getId()),
                buildSubjectTaught(teacherId, savedSubjects.get(1).getId()),
                buildSubjectTaught(teacherId, savedSubjects.get(2).getId())
        );
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateSubjectTaught() {
        // when
        SubjectTaught savedSubjectTaught = subjectTaughtController.createSubjectTaught(Mono.just(subjectsTaught.get(0)))
                .block();

        // then
        assertNotNull(savedSubjectTaught);
        assertNotNull(savedSubjectTaught.getId());
    }
}
