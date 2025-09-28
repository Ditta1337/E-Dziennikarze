package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildTeacherUnavailability;
import static com.edziennikarze.gradebook.util.ObjectsBuilder.buildUser;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.config.TestSecurityConfig;
import com.edziennikarze.gradebook.exception.CollisionException;
import com.edziennikarze.gradebook.planner.restriction.teacherunavailability.util.TeacherUnavailabilityTestDatabaseCleaner;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
@Import(TestSecurityConfig.class)
class TeacherUnavailabilityControllerIntTest {

    @Autowired
    private TeacherUnavailabilityController teacherUnavailabilityController;

    @Autowired
    private TeacherUnavailabilityTestDatabaseCleaner teacherUnavailabilityTestDatabaseCleaner;

    @Autowired
    private TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private LoggedInUserService loggedInUserService;

    private List<User> teachers;

    private List<TeacherUnavailability> teacherUnavailabilities;

    @BeforeEach
    void setUp() {
        setUpTeachers();
        setUpTeacherUnavailability();
    }

    @AfterEach
    void tearDown() {
        teacherUnavailabilityRepository.deleteAll();
    }

    @Test
    void shouldCreateTeacherUnavailability() {
        // when
        List<TeacherUnavailability> createdTeacherUnavailabilities = teacherUnavailabilities.stream()
                .map(teacherUnavailability -> teacherUnavailabilityController.createTeacherUnavailability(Mono.just(teacherUnavailability))
                        .block())
                .toList();

        // then
        assertEquals(teacherUnavailabilities.size(), createdTeacherUnavailabilities.size());
        createdTeacherUnavailabilities.forEach(teacherUnavailability -> assertNotNull(teacherUnavailability.getId()));
    }

    @Test
    void shouldThrowCollisionExceptionWhenCollisionDetected() {
        // given
        teacherUnavailabilityRepository.saveAll(teacherUnavailabilities)
                .collectList()
                .block();
        UUID firstTeacherId = teachers.getFirst()
                .getId();
        TeacherUnavailability collidingTeacherUnavailability = buildTeacherUnavailability(firstTeacherId, LocalTime.of(5, 0), LocalTime.of(8, 30),
                DayOfWeek.MONDAY);

        // when
        Mono<TeacherUnavailability> resultMono = teacherUnavailabilityController.createTeacherUnavailability(Mono.just(collidingTeacherUnavailability));

        // then
        assertThrows(CollisionException.class, resultMono::block);
    }

    @Test
    void shouldGetTeachersUnavailability() {
        // given
        teacherUnavailabilityRepository.saveAll(teacherUnavailabilities)
                .collectList()
                .block();
        UUID firstTeacherId = teachers.getFirst()
                .getId();
        UUID secondTeacherId = teachers.getLast()
                .getId();
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        List<TeacherUnavailability> firstTeacherUnavailabilities = teacherUnavailabilityController.getTeacherUnavailability(firstTeacherId)
                .collectList()
                .block();
        List<TeacherUnavailability> secondTeacherUnavailabilities = teacherUnavailabilityController.getTeacherUnavailability(secondTeacherId)
                .collectList()
                .block();

        // then
        assertEquals(4, firstTeacherUnavailabilities.size());
        assertEquals(1, secondTeacherUnavailabilities.size());
    }

    @Test
    void shouldUpdateTeacherUnavailability() {
        // given
        List<TeacherUnavailability> savedTeacherUnavailabilities = teacherUnavailabilityRepository.saveAll(teacherUnavailabilities)
                .collectList()
                .block();

        TeacherUnavailability originalTeacherUnavailability = savedTeacherUnavailabilities.getFirst();
        TeacherUnavailability updatedTeacherUnavailability = buildTeacherUnavailability(originalTeacherUnavailability.getTeacherId(),
                originalTeacherUnavailability.getStartTime(), originalTeacherUnavailability.getEndTime(), DayOfWeek.WEDNESDAY);
        updatedTeacherUnavailability.setId(originalTeacherUnavailability.getId());
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        TeacherUnavailability savedUpdatedTeacherUnavailability = teacherUnavailabilityController.updateTeacherUnavailability(Mono.just(updatedTeacherUnavailability))
                .block();

        // then
        assertEquals(updatedTeacherUnavailability, savedUpdatedTeacherUnavailability);
        assertNotEquals(originalTeacherUnavailability, savedUpdatedTeacherUnavailability);
    }

    @Test
    void shouldDeleteTeacherUnavailability() {
        // given
        List<TeacherUnavailability> savedTeacherUnavailabilities = teacherUnavailabilityRepository.saveAll(teacherUnavailabilities)
                .collectList()
                .block();
        when(loggedInUserService.isSelfOrAllowedRoleElseThrow(any(), any(Role[].class))).thenReturn(Mono.just(true));

        // when
        teacherUnavailabilityController.deleteTeacherUnavailability(savedTeacherUnavailabilities.getFirst().getId()
                )
                .block();
        List<TeacherUnavailability> savedTeacherUnavailabilitiesAfterDeletion = teacherUnavailabilityRepository.findAll()
                .collectList()
                .block();

        // then
        assertNotEquals(teacherUnavailabilities.size(), savedTeacherUnavailabilitiesAfterDeletion.size());
    }

    private void setUpTeachers() {
        List<User> teachersToSave = List.of(buildUser("artur@gmail.com", Role.TEACHER, true, true), buildUser("maciek@gmail.com", Role.TEACHER, true, true));

        teachers = userRepository.saveAll(teachersToSave)
                .collectList()
                .block();
    }

    private void setUpTeacherUnavailability() {
        UUID firstTeacherId = teachers.getFirst()
                .getId();
        UUID secondTeacherId = teachers.getLast()
                .getId();

        teacherUnavailabilities = List.of(buildTeacherUnavailability(firstTeacherId, LocalTime.of(7, 0), LocalTime.of(8, 30), DayOfWeek.MONDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(18, 30), LocalTime.of(20, 15), DayOfWeek.MONDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(8, 0), LocalTime.of(10, 30), DayOfWeek.TUESDAY),
                buildTeacherUnavailability(firstTeacherId, LocalTime.of(17, 0), LocalTime.of(21, 30), DayOfWeek.TUESDAY),
                buildTeacherUnavailability(secondTeacherId, LocalTime.of(13, 0), LocalTime.of(17, 30), DayOfWeek.MONDAY));
    }

}