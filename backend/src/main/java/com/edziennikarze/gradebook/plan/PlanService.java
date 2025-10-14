package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.plan.dto.PlanTeacher;
import com.edziennikarze.gradebook.plan.dto.PlanUnavailability;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailability;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.property.Property;
import com.edziennikarze.gradebook.property.PropertyRepository;
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
    private final PropertyRepository propertyRepository;

    private final static List<String> lessonPropertiesNames = List.of(
            "schoolDayStartTime",
            "lessonDurationMinutes",
            "shortBreakDurationMinutes",
            "longBreakDurationMinutes",
            "longBreakAfterLessons",
            "maxLessonsPerDay"
    );

    public Mono<Plan> initializePlan(Mono<Plan> planMono) {
        return planMono
                .flatMap(this::enrichPlanWithUniqueGroupCombinations)
                .flatMap(this::enrichPlanWithTeachers)
                .flatMap(this::enrichPlanWithTeacherUnavailabilities);
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
                .map(user -> PlanTeacher.builder()
                        .teacherId(user.getId())
                        .build())
                .collectList()
                .map(teachers -> {
                    plan.setTeachers(teachers);
                    return plan;
                });
    }

    private Mono<Plan> enrichPlanWithTeacherUnavailabilities(Plan plan) {
        if (plan.getTeachers() == null) {
            plan.setTeachers(new ArrayList<>());
        }

        return propertyRepository.findAllByNameIn(lessonPropertiesNames)
                .collectList()
                .flatMap(properties -> {
                    Map<String, String> props = makePropertiesMap(properties);

                    LocalTime dayStart = LocalTime.parse(props.get("schoolDayStartTime"));
                    int lessonMinutes = Integer.parseInt(props.get("lessonDurationMinutes"));
                    int shortBreak = Integer.parseInt(props.get("shortBreakDurationMinutes"));
                    int longBreak = Integer.parseInt(props.get("longBreakDurationMinutes"));
                    int longBreakAfter = Integer.parseInt(props.get("longBreakAfterLessons"));
                    int maxLessons = Integer.parseInt(props.get("maxLessonsPerDay"));

                    List<LocalTime> lessonStartTimes = new ArrayList<>();
                    LocalTime current = dayStart;
                    for (int i = 1; i <= maxLessons; i++) {
                        lessonStartTimes.add(current);
                        current = current.plusMinutes(lessonMinutes);
                        current = current.plusMinutes(i == longBreakAfter ? longBreak : shortBreak);
                    }

                    return teacherUnavailabilityRepository.findAll()
                            .collectList()
                            .map(unavailabilities -> {
                                Map<UUID, List<PlanUnavailability>> map = new HashMap<>();

                                for (TeacherUnavailability un : unavailabilities) {
                                    List<PlanUnavailability> list = map.computeIfAbsent(un.getTeacherId(), k -> new ArrayList<>());

                                    for (int lessonIndex = 0; lessonIndex < lessonStartTimes.size(); lessonIndex++) {
                                        LocalTime start = lessonStartTimes.get(lessonIndex);
                                        LocalTime end = start.plusMinutes(lessonMinutes);

                                        if (!end.isBefore(un.getStartTime()) && !start.isAfter(un.getEndTime())) {
                                            list.add(PlanUnavailability.builder()
                                                    .day(un.getWeekDay().getValue()-1)
                                                    .lesson(lessonIndex )
                                                    .build());
                                        }
                                    }
                                }

                                for (PlanTeacher teacher : plan.getTeachers()) {
                                    teacher.setUnavailability(map.getOrDefault(teacher.getTeacherId(), List.of()));
                                }

                                return plan;
                            });
                });
    }


    private Map<String, String> makePropertiesMap(List<Property> properties) {
        Map<String, String> map = new HashMap<>();
        for (Property p : properties) {
            map.put(
                    p.getName(),
                    p.getValue() != null ? p.getValue().toString() : p.getDefaultValue().toString()
            );
        }
        return map;
    }
}
