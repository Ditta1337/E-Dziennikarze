package com.edziennikarze.gradebook.group;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.group.util.GroupTestDatabaseCleaner;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroup;
import static org.junit.jupiter.api.Assertions.*;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class GroupControllerIntTest {

    @Autowired
    private GroupController groupController;

    @Autowired
    private GroupTestDatabaseCleaner groupTestDatabaseCleaner;

    @Autowired
    private GroupRepository groupRepository;

    private List<Group> groups;

    @BeforeEach
    void setUp() {
        setUpGroups();
    }

    @AfterEach
    void tearDown() {
        groupTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateGroups() {
        // when
        List<Group> createdGroups = groups.stream()
                .map(group -> groupController.createGroup(Mono.just(group))
                        .block())
                .toList();

        // then
        assertEquals(groups.size(), createdGroups.size());
        createdGroups.forEach(group -> assertNotNull(group.getId()));
    }

    @Test
    void shouldGetAllGroups() {
        // given
        List<Group> savedGroups = groupRepository.saveAll(groups)
                .collectList()
                .block();

        // when
        List<Group> allGroups = groupController.getAllGroups()
                .collectList()
                .block();

        // then
        assertEquals(savedGroups, allGroups);
    }

    @Test
    void shouldGetAllClasses() {
        // given
        groupRepository.saveAll(groups)
                .collectList()
                .block();

        // when
        List<Group> allClasses = groupController.getAllClasses()
                .collectList()
                .block();

        // then
        assertEquals(6, allClasses.size());
    }

    @Test
    void shouldGetAllByStartYear() {
        // given
        groupRepository.saveAll(groups)
                .collectList()
                .block();

        // when
        List<Group> firstClasses = groupController.getAllGroupsStartYear(1)
                .collectList()
                .block();

        List<Group> secondClasses = groupController.getAllGroupsStartYear(2)
                .collectList()
                .block();

        List<Group> thirdClasses = groupController.getAllGroupsStartYear(3)
                .collectList()
                .block();

        List<Group> fourthClasses = groupController.getAllGroupsStartYear(4)
                .collectList()
                .block();

        List<Group> fifthClasses = groupController.getAllGroupsStartYear(5)
                .collectList()
                .block();

        // then
        assertEquals(3, firstClasses.size());
        assertEquals(1, secondClasses.size());
        assertEquals(1, thirdClasses.size());
        assertEquals(1, fourthClasses.size());
        assertEquals(2, fifthClasses.size());
    }

    @Test
    void shouldUpdateGroup() {
        // given
        List<Group> savedGroups = groupRepository.saveAll(groups)
                .collectList()
                .block();

        Group originalGroup = savedGroups.getFirst();
        Group updatedOriginalGroup = buildGroup(2, "updated_a", false);
        updatedOriginalGroup.setId(originalGroup.getId());

        // when
        Group savedUpdatedGroup = groupController.updateGroup(Mono.just(updatedOriginalGroup))
                .block();

        // then
        assertEquals(updatedOriginalGroup, savedUpdatedGroup);
        assertNotEquals(originalGroup, savedUpdatedGroup);
        assertFalse(savedUpdatedGroup.isClass());
    }

    @Test
    void shouldIncrementAllGroupsStartYear() {
        // given
        List<Group> savedGroups = groupRepository.saveAll(groups)
                .collectList()
                .block();

        // when
        List<Group> groupsWithIncrementedStartYears = groupController.incrementAllGroupsCode()
                .collectList()
                .block();

        // then
        for (int i = 0; i < savedGroups.size(); i++) {
            String expectedCode = getExpectedGroupCode(savedGroups.get(i).getGroupCode());
            List<Group> groupsWithExpectedCode = groupsWithIncrementedStartYears.stream()
                    .filter(g -> g.getGroupCode().equals(expectedCode))
                    .toList();
            assertEquals(1, groupsWithExpectedCode.size());
        }
    }

    @Test
    void shouldDeleteGroup() {
        // given
        List<Group> savedGroups = groupRepository.saveAll(groups)
                .collectList()
                .block();

        // when
        groupController.deleteGroup(savedGroups.getFirst()
                        .getId())
                .block();
        List<Group> allGroupsAfterDelete = groupRepository.findAll()
                .collectList()
                .block();

        // then
        assertEquals(savedGroups.size() - 1, allGroupsAfterDelete.size());
    }

    private void setUpGroups() {
        groups = List.of(buildGroup(1, "1_a", true), buildGroup(1, "1_b", true), buildGroup(1, "1_c", true), buildGroup(2, "2_a", true), buildGroup(3, "2_b", true),
                buildGroup(4, "2_c", true), buildGroup(5, "2_c_ang_advanced", false), buildGroup(5, "2_c_ang_standard", false));
    }

    private String getExpectedGroupCode(String initialCode) {
        List<String> parts = Arrays.asList(initialCode.split("_"));
        int year = Integer.parseInt(parts.get(0)) + 1;
        String otherParts = String.join("_", parts.subList(1, parts.size()));
        return year + "_" + otherParts;
    }
}
