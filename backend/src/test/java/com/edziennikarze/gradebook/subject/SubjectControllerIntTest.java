package com.edziennikarze.gradebook.subject;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildSubject;
import static org.junit.jupiter.api.Assertions.*;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.subject.utils.SubjectTestDatabaseCleaner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class SubjectControllerIntTest {

    @Autowired
    private SubjectController subjectController;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectTestDatabaseCleaner subjectTestDatabaseCleaner;

    private List<Subject> subjects = new ArrayList<>();

    @BeforeEach
    void setUp() {
        setUpSubjects();
    }

    @AfterEach
    void tearDown() {
        subjectTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateSubject() {
        // when
        List<Mono<Subject>> savedSubjects = subjects.stream()
                .map(subject -> subjectController.createSubject(Mono.just(subject)))
                .toList();

        // then
        assertEquals(savedSubjects.size(), subjects.size());
        savedSubjects.forEach(savedSubjectMono -> {
            Subject savedSubject = savedSubjectMono.block();
            assertNotNull(savedSubject.getId());
        });
    }

    @Test
    void shouldGetAllSubjects() {
        // given
        subjectRepository.saveAll(subjects)
                .collectList()
                .block();

        // when
        List<Subject> retrievedSubjects = subjectController.getAllSubjects()
                .collectList()
                .block();

        // then
        assertNotNull(retrievedSubjects);
        assertEquals(subjects.size(), retrievedSubjects.size());
    }

    @Test
    void shouldDeleteSubjectByUUID() {
        // given
        List<Subject> savedSubjects = subjects.stream()
                .map(subject -> subjectRepository.save(subject)
                        .block())
                .toList();
        UUID subjectIdToDelete = savedSubjects.getFirst()
                .getId();

        // when
        subjectController.deleteSubject(subjectIdToDelete)
                .block();

        // then
        assertFalse(subjectRepository.existsById(subjectIdToDelete)
                .block());
        assertEquals(subjects.size() - 1, subjectRepository.findAll()
                .count()
                .block());
    }

    private void setUpSubjects() {
        subjects = List.of(buildSubject("Matematyka"), buildSubject("Fizyka"), buildSubject("J. Polski"));
    }
}
