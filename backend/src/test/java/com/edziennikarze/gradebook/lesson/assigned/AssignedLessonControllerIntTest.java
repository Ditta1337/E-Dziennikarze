package com.edziennikarze.gradebook.lesson.assigned;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.util.AssignedLessonTestDatabaseCleaner;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.UserRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
class AssignedLessonControllerIntTest {

    @Autowired
    private AssignedLessonController assignedLessonController;

    @Autowired
    private AssignedLessonTestDatabaseCleaner assignedLessonTestDatabaseCleaner;

    @Autowired
    private AssignedLessonRepository assignedLessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlannedLessonRepository plannedLessonRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        assignedLessonTestDatabaseCleaner.cleanAll();
    }
}
