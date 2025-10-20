package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.plan.dto.Plan;
import com.edziennikarze.gradebook.plan.dto.PlanGroup;
import com.edziennikarze.gradebook.plan.dto.PlanTeacher;
import com.edziennikarze.gradebook.plan.dto.PlanUnavailability;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailability;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import com.edziennikarze.gradebook.plan.util.PlanTestDatabaseCleaner;
import com.edziennikarze.gradebook.property.PropertyRepository;
import com.edziennikarze.gradebook.solver.SolverService;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.*;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildGroup;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class PlanControllerIntTest {

    @Autowired
    private PlanController planController;

    @Autowired
    private PlanTestDatabaseCleaner planTestDatabaseCleaner;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @MockBean
    private SolverService solverService;

    private List<User> students;

    private List<User> teachers;

    private List<Group> groups;

    private List<StudentGroup> studentGroups;

    private List<TeacherUnavailability> teacherUnavailabilities;

    @BeforeEach
    void setUp() {
        setUpStudents();
        setUpTeachers();
        setUpGroups();
        setUpStudentGroups();
        setUpTeacherUnavailability();

        when(solverService.calculatePlan(any())).thenReturn(Mono.empty());

    }

    @AfterEach
    void cleanUp() {
        planTestDatabaseCleaner.cleanAll();
    }


    @Test
    void shouldEnrichPlanCorrectly() {
        // given
        Plan plan = Plan.builder()
                .goals(List.of())
                .groups(groups.stream()
                        .map(g -> PlanGroup.builder()
                                .groupId(g.getId())
                                .subjects(List.of())
                                .build())
                        .toList())
                .teachers(teachers.stream()
                        .map(t -> PlanTeacher.builder()
                                .teacherId(t.getId())
                                .build())
                        .toList())
                .uniqueGroupCombinations(List.of())
                .lessonsPerDay(5)
                .build();

        teacherUnavailabilityRepository.saveAll(teacherUnavailabilities)
                .collectList()
                .block();

        // when
        Plan enrichedPlan = planController.initializePlan(Mono.just(plan)).block();

        // then

        //gourps
        UUID groupAId = groups.get(0)
                .getId();
        UUID groupBId = groups.get(1)
                .getId();
        UUID groupWFABId = groups.get(2)
                .getId();
        UUID groupBPId = groups.get(3)
                .getId();
        UUID groupBRId = groups.get(4)
                .getId();
        UUID groupAPId = groups.get(5)
                .getId();
        UUID groupARId = groups.get(6)
                .getId();
        List<Set<UUID>> expected = List.of(
                Set.of(groupWFABId,groupAId,groupAPId),
                Set.of(groupWFABId,groupAId,groupARId),
                Set.of(groupWFABId,groupBId,groupBPId),
                Set.of(groupWFABId,groupBId,groupBRId)
        );

        List<Set<UUID>> actual = enrichedPlan.getUniqueGroupCombinations().stream()
                .map(HashSet::new)
                .collect(Collectors.toList());

        assertTrue(actual.containsAll(expected) && expected.containsAll(actual),
                "The unique group combinations do not match, ignoring order");


        //teachers
        List<PlanUnavailability> expectedUnavailabilities = List.of(
                PlanUnavailability.builder().day(0).lesson(0).build(),
                PlanUnavailability.builder().day(0).lesson(1).build(),
                PlanUnavailability.builder().day(0).lesson(6).build(),
                PlanUnavailability.builder().day(0).lesson(7).build(),
                PlanUnavailability.builder().day(1).lesson(1).build(),
                PlanUnavailability.builder().day(1).lesson(2).build(),
                PlanUnavailability.builder().day(1).lesson(3).build()
        );

        List<PlanUnavailability> actualUnavailabilities = enrichedPlan.getTeachers().stream()
                .flatMap(t -> t.getUnavailability().stream())
                .toList();
        assertTrue(actualUnavailabilities.containsAll(expectedUnavailabilities)
                        && expectedUnavailabilities.containsAll(actualUnavailabilities),
                "The teacher unavailabilities do not match, ignoring order");
    }

    private void setUpStudents() {
        List<User> studentsToSave = List.of(
                buildUser("maciek@gmail.com", Role.STUDENT, true, true),
                buildUser("artur@gmail.com", Role.STUDENT, true, true),
                buildUser("szymetron@gmail.com", Role.STUDENT, true, true),
                buildUser("szymon@gmail.com", Role.STUDENT, true, true)
        );
        students = userRepository.saveAll(studentsToSave)
                .collectList()
                .block();
    }

    private void setUpTeachers() {
        List<User> teachersToSave = List.of(buildUser("szywoj@gmail.com", Role.TEACHER, true, true), buildUser("maciek@gmail.com", Role.TEACHER, true, true));

        teachers = userRepository.saveAll(teachersToSave)
                .collectList()
                .block();
    }

    private void setUpStudentGroups() {
        UUID maciekId = students.get(0)
                .getId();
        UUID arturId = students.get(1)
                .getId();
        UUID szymonId = students.get(2)
                .getId();
        UUID symetronId = students.get(3)
                .getId();

        UUID groupAId = groups.get(0)
                .getId();
        UUID groupBId = groups.get(1)
                .getId();
        UUID groupWFABId = groups.get(2)
                .getId();
        UUID groupBPId = groups.get(3)
                .getId();
        UUID groupBRId = groups.get(4)
                .getId();
        UUID groupAPId = groups.get(5)
                .getId();
        UUID groupARId = groups.get(6)
                .getId();

        List<StudentGroup> studentGroupsToSave = List.of(
                buildStudentGroup(maciekId, groupWFABId),
                buildStudentGroup(maciekId, groupAId),
                buildStudentGroup(maciekId, groupAPId),

                buildStudentGroup(arturId, groupWFABId),
                buildStudentGroup(arturId, groupAId),
                buildStudentGroup(arturId, groupARId),

                buildStudentGroup(szymonId, groupWFABId),
                buildStudentGroup(szymonId, groupBId),
                buildStudentGroup(szymonId, groupBPId),

                buildStudentGroup(symetronId, groupWFABId),
                buildStudentGroup(symetronId, groupBId),
                buildStudentGroup(symetronId, groupBRId)
        );
        studentGroups = studentGroupRepository.saveAll(studentGroupsToSave)
                .collectList()
                .block();
    }

    private void setUpGroups() {
        List<Group> groupsToSave = List.of(
                buildGroup(1, "1_A", true),
                buildGroup(1, "1_B", true),
                buildGroup(1, "1_AB_WF", true),
                buildGroup(1, "1_A_ANG_PODSTAWOWY", true),
                buildGroup(1, "1_A_ANG_ROZSZERZONY", true),
                buildGroup(1, "1_B_ANG_PODSTAWOWY", true),
                buildGroup(1, "1_B_ANG_ROZSZERZONY", true)
                );
        groups = groupRepository.saveAll(groupsToSave)
                .collectList()
                .block();
    }

    private void setUpTeacherUnavailability() {
        UUID firstTeacherId = teachers.getFirst()
                .getId();
        UUID secondTeacherId = teachers.getLast()
                .getId();

        teacherUnavailabilities = List.of(
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(7, 0), LocalTime.of(8, 30), DayOfWeek.MONDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(18, 30), LocalTime.of(20, 15), DayOfWeek.MONDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(8, 0), LocalTime.of(10, 30), DayOfWeek.TUESDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(17, 0), LocalTime.of(21, 30), DayOfWeek.TUESDAY),
                buildTeacherUnavailability(secondTeacherId, LocalTime.of(13, 0), LocalTime.of(17, 30), DayOfWeek.MONDAY));
    }

}
