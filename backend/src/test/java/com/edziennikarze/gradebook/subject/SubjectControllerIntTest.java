package com.edziennikarze.gradebook.subject;

import static org.junit.jupiter.api.Assertions.*;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.subject.utils.SubjectTestDatabaseCleaner;
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

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port=0"
)
@ImportTestcontainers(PostgresTestContainerConfig.class)
public class SubjectControllerIntTest {

    @Autowired
    private SubjectController subjectController;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectTestDatabaseCleaner databaseCleaner;

    private List<Subject> subjects = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        subjects = List.of(
                buildSubject("Matematyka"),
                buildSubject("Fizyka"),
                buildSubject("J. Polski")
        );
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanAll();
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
    void shouldGetAllSubjects(){
        // given
        subjects.forEach(subject -> subjectRepository.save(subject).block());


        // when
        List<Subject> retrievedSubjects = subjectController.getAllSubjects()
                .collectList()
                .block();

        System.out.println(subjects.size());

        // then
        assertNotNull(retrievedSubjects);
        assertEquals(subjects.size(), retrievedSubjects.size());
    }

    @Test
    void shouldDeleteSubjectByUUID() {
        // given
        List<Subject> savedSubjects = subjects.stream()
                .map(subject -> subjectRepository.save(subject).block())
                .toList();
        UUID subjectIdToDelete = savedSubjects.get(0).getId();

        // when
        subjectController.deleteSubject(subjectIdToDelete).block();

        // then
        assertFalse(subjectRepository.existsById(subjectIdToDelete).block());
        assertEquals(subjects.size() - 1, subjectRepository.findAll().count().block());
    }

    private Subject buildSubject(String name) {
        return Subject.builder()
                .name(name)
                .build();
    }
}
