package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.plan.dto.Plan;
import com.edziennikarze.gradebook.plan.dto.PlanTeacher;
import com.edziennikarze.gradebook.plan.dto.PlanUnavailability;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailability;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.property.PropertyService;
import com.edziennikarze.gradebook.solver.SolverService;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    private final PropertyService propertyService;

    private final SolverService solverService;

    private static final List<String> LESSON_PROPERTIES_NAMES = List.of(
            "schoolDayStartTime",
            "lessonDurationMinutes",
            "shortBreakDurationMinutes",
            "longBreakDurationMinutes",
            "longBreakAfterLessons",
            "maxLessonsPerDay"
    );

    public Mono<Plan> initializePlan(Mono<Plan> planMono) {
        Mono<Plan> enrichedPlan = planMono
                .flatMap(this::enrichPlanWithRooms)
                .flatMap(this::enrichPlanWithUniqueGroupCombinations)
                .flatMap(this::enrichPlanWithTeachers)
                .flatMap(this::enrichPlanWithTeacherUnavailabilities);

        return enrichedPlan
                .flatMap(plan ->
                    solverService.calculatePlan(plan)
                    .then(Mono.just(plan))
                );
    }

    public Mono<Plan> enrichPlanWithRooms(Plan plan) {
        Set<UUID> roomIds = plan.getGroups().stream()
                .flatMap(group -> group.getSubjects().stream())
                .map(subject -> subject.getRoom().getAllowed())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        plan.setRooms(new ArrayList<>(roomIds));
        return Mono.just(plan);
    }

    private Mono<Plan> enrichPlanWithUniqueGroupCombinations(Plan plan) {
        return userRepository.findAllByRole(Role.STUDENT)
                .flatMap(student -> studentGroupRepository.findAllByStudentId(student.getId())
                        .map(StudentGroup::getGroupId)
                        .sort()
                        .collectList())
                .collect(Collectors.toSet())
                .map(uniqueCombinations -> {
                    plan.setUniqueGroupCombinations(new ArrayList<>(uniqueCombinations));
                    return plan;
                });
    }

    private Mono<Plan> enrichPlanWithTeachers(Plan plan) {
        return userRepository.findAllByRole(Role.TEACHER)
                .map(teacher -> PlanTeacher.builder()
                        .teacherId(teacher.getId())
                        .build())
                .collectList()
                .map(teachers -> {
                    plan.setTeachers(teachers);
                    return plan;
                });
    }

    private Mono<Plan> enrichPlanWithTeacherUnavailabilities(Plan plan) {
        Mono<Map<String, Object>> propertiesMono = propertyService.getPropertiesAsMap(LESSON_PROPERTIES_NAMES);
        Mono<List<TeacherUnavailability>> unavailabilitiesMono = teacherUnavailabilityRepository.findAll().collectList();

        return Mono.zip(propertiesMono, unavailabilitiesMono)
                .map(tuple -> {
                    Map<String, Object> properties = tuple.getT1();
                    List<TeacherUnavailability> unavailabilities = tuple.getT2();

                    List<LocalTime> lessonStartTimes = calculateLessonStartTimes(properties);
                    int lessonMinutes = (Integer) properties.get("lessonDurationMinutes");

                    Map<UUID, List<PlanUnavailability>> unavailabilityMap = buildTeacherUnavailabilityMap(
                            unavailabilities, lessonStartTimes, lessonMinutes
                    );

                    attachUnavailabilitiesToPlan(plan, unavailabilityMap);

                    return plan;
                });
    }

    private List<LocalTime> calculateLessonStartTimes(Map<String, Object> properties) {
        LocalTime dayStart = (LocalTime) properties.get("schoolDayStartTime");
        int lessonMinutes = (Integer) properties.get("lessonDurationMinutes");
        int shortBreak = (Integer) properties.get("shortBreakDurationMinutes");
        int longBreak = (Integer) properties.get("longBreakDurationMinutes");
        int longBreakAfter = (Integer) properties.get("longBreakAfterLessons");
        int maxLessons = (Integer) properties.get("maxLessonsPerDay");

        List<LocalTime> lessonStartTimes = new ArrayList<>();
        LocalTime currentTime = dayStart;

        for (int i = 1; i <= maxLessons; i++) {
            lessonStartTimes.add(currentTime);
            currentTime = currentTime.plusMinutes(lessonMinutes);
            int breakToAdd = (i == longBreakAfter) ? longBreak : shortBreak;
            currentTime = currentTime.plusMinutes(breakToAdd);
        }
        return lessonStartTimes;
    }

    private Map<UUID, List<PlanUnavailability>> buildTeacherUnavailabilityMap(
            List<TeacherUnavailability> unavailabilities, List<LocalTime> lessonStartTimes, int lessonMinutes) {

        Map<UUID, List<PlanUnavailability>> map = new HashMap<>();

        for (TeacherUnavailability un : unavailabilities) {
            List<PlanUnavailability> teacherUnavailabilitySlots = map.computeIfAbsent(un.getTeacherId(), k -> new ArrayList<>());

            for (int lessonIndex = 0; lessonIndex < lessonStartTimes.size(); lessonIndex++) {
                LocalTime lessonStart = lessonStartTimes.get(lessonIndex);
                LocalTime lessonEnd = lessonStart.plusMinutes(lessonMinutes);

                if (isOverlapping(lessonStart, lessonEnd, un.getStartTime(), un.getEndTime())) {
                    PlanUnavailability slot = PlanUnavailability.builder()
                            .day(un.getWeekDay().getValue() - 1)
                            .lesson(lessonIndex)
                            .build();
                    teacherUnavailabilitySlots.add(slot);
                }
            }
        }
        return map;
    }

    private void attachUnavailabilitiesToPlan(Plan plan, Map<UUID, List<PlanUnavailability>> unavailabilityMap) {
        for (PlanTeacher teacher : plan.getTeachers()) {
            teacher.setUnavailability(unavailabilityMap.getOrDefault(teacher.getTeacherId(), Collections.emptyList()));
        }
    }

    private boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
}
