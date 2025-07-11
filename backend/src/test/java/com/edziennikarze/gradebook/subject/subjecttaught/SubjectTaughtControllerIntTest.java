package com.edziennikarze.gradebook.subject.subjecttaught;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.subject.utils.SubjectTestDatabaseCleaner;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
class SubjectTaughtControllerIntTest {

    @Autowired
    private SubjectTaughtController subjectTaughtController;

    @Autowired
    private SubjectRepository subjectRepository;


    @Autowired
    private SubjectTaughtRepository subjectTaughtRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectTestDatabaseCleaner databaseCleaner;

    private List<Subject> subjects;

    private User teacher;

    private List<SubjectTaught> subjectsTaught;

    @BeforeEach
    void setUp() {
        List<Subject> subjectsToSave = List.of(buildSubject("Matematyka"), buildSubject("Fizyka"), buildSubject("J. Polski"));
        subjects = subjectRepository.saveAll(subjectsToSave)
                .collectList()
                .block();

        teacher = userRepository.save(buildUser("artur@gmail.com", Role.TEACHER, true, true))
                .block();
        UUID teacherId = teacher.getId();

        subjectsTaught = List.of(buildSubjectTaught(teacherId,
                        subjects.get(0)
                                .getId()),
                buildSubjectTaught(teacherId,
                        subjects.get(1)
                                .getId()),
                buildSubjectTaught(teacherId,
                        subjects.get(2)
                                .getId()));
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateSubjectTaught() {
        // when
        SubjectTaught savedSubjectTaught = subjectTaughtController.createSubjectTaught(Mono.just(subjectsTaught.getFirst()))
                .block();

        // then
        assertNotNull(savedSubjectTaught);
        assertNotNull(savedSubjectTaught.getId());
    }

    @Test
    void shouldCreateSubjectsTaught() {
        // when
        List<SubjectTaught> savedSubjectsTaught = subjectTaughtController.createSubjectsTaught(Flux.fromIterable(subjectsTaught))
                .collectList()
                .block();

        // then
        assertNotNull(savedSubjectsTaught);
        assertEquals(subjectsTaught.size(), savedSubjectsTaught.size());
    }

    @Test
    void shouldGetAllSubjectTaught() {
        // given
        subjectTaughtRepository.saveAll(subjectsTaught)
                .collectList()
                .block();

        // when
        List<Subject> retrievedSubjectsTaught = subjectTaughtController.getAllSubjectsTaught()
                .collectList()
                .block();

        // then
        assertNotNull(retrievedSubjectsTaught);
        assertEquals(subjects.size(), retrievedSubjectsTaught.size());
    }

    @Test
    void shouldGetAllTeachersSubjectTaught() {
        // given
        subjectTaughtRepository.saveAll(subjectsTaught)
                .collectList()
                .block();

        UUID teacherId = subjectsTaught.getFirst()
                .getTeacherId();

        // when
        List<Subject> teachersSubjectsTaught = subjectTaughtController.getSubjectsTaughtByTeacher(teacherId)
                .collectList()
                .block();

        // then
        assertNotNull(teachersSubjectsTaught);
        assertEquals(subjectsTaught.size(), teachersSubjectsTaught.size());
    }

    @Test
    void shouldGetAllTeachersTeachingSubject() {
        // given
        subjectTaughtRepository.saveAll(subjectsTaught)
                .collectList()
                .block();

        UUID subjectId = subjects.getFirst()
                .getId();

        // when
        List<User> teachersSubjectsTaught = subjectTaughtController.getTeachersTeachingSubject(subjectId)
                .collectList()
                .block();

        // then
        assertNotNull(teachersSubjectsTaught);
        assertEquals(1, teachersSubjectsTaught.size());
    }
}
