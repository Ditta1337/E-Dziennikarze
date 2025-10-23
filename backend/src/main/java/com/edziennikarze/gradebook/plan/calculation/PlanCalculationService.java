package com.edziennikarze.gradebook.plan.calculation;

import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculation;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculationsSummary;
import com.edziennikarze.gradebook.plan.calculation.dto.request.*;
import com.edziennikarze.gradebook.plan.configuration.PlanConfigurationRepository;
import com.edziennikarze.gradebook.property.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  PlanCalculationService {

    private final PlanCalculationRepository planCalculationRepository;

    private final PlanConfigurationRepository planConfigurationRepository;

    private final PropertyService propertyService;

    private final ObjectMapper objectMapper;

    private static final List<String> LESSON_PROPERTIES_NAMES = List.of(
            "schoolDayStartTime",
            "lessonDurationMinutes",
            "shortBreakDurationMinutes",
            "longBreakDurationMinutes",
            "longBreakAfterLessons"
    );

    public Mono<PlanCalculationResponse> savePlanCalculation(Mono<PlanCalculationRequest> planCalculationRequestMono) {
        return planCalculationRequestMono.flatMap(planCalculationRequest ->
                planConfigurationRepository.updateCalculatedStatus(planCalculationRequest.getPlanId(), true)
                        .then(mapToPlanCalculation(planCalculationRequest))
        );
    }

    public Mono<PlanCalculationResponse> getAllPlanCalculationsForPlan(UUID id) {
        return planCalculationRepository.findById(id)
                .map(planCalculation -> PlanCalculationResponse.from(planCalculation, objectMapper));
    }

    public Flux<PlanCalculationsSummary> getPlanCalculationsSummary(UUID planId) {
        return planCalculationRepository.findAllSummaryByPlanId(planId);
    }

    private Mono<PlanCalculationResponse> mapToPlanCalculation(PlanCalculationRequest request) {
        return propertyService.getPropertiesAsMap(LESSON_PROPERTIES_NAMES)
                .flatMap(properties -> {
                    List<PlannedLesson> plannedLessons = generatePlannedLessons(request, properties);

                    PlanCalculation planCalculation = new PlanCalculation();
                    planCalculation.setName(request.getName());
                    planCalculation.setPlanId(request.getPlanId());
                    planCalculation.setCalculation(plannedLessons, objectMapper);

                    return planCalculationRepository.save(planCalculation);
                })
                .map(savedPlan -> PlanCalculationResponse.from(savedPlan, objectMapper));
    }

    private List<PlannedLesson> generatePlannedLessons(PlanCalculationRequest request, Map<String, Object> properties) {
        Set<UUID> validGroupIds = request.getGroups().stream()
                .map(PlanCalculationRequestGroup::getGroupId)
                .collect(Collectors.toSet());

        return request.getTeachers().stream()
                .flatMap(teacher -> teacher.getSchedule().stream()
                        .map(schedule -> createPlannedLesson(teacher.getTeacherId(), schedule, properties))
                )
                .filter(plannedLesson -> validGroupIds.contains(plannedLesson.getGroupId()))
                .toList();
    }

    private PlannedLesson createPlannedLesson(UUID teacherId, PlanCalculationRequestTeacherLesson schedule, Map<String, Object> properties) {
        return PlannedLesson.builder()
                .weekDay(DayOfWeek.of(schedule.getDay() + 1))
                .subjectId(schedule.getSubjectId())
                .roomId(schedule.getRoomId())
                .groupId(schedule.getGroupId())
                .active(true)
                .teacherId(teacherId)
                .startTime(calculateLessonStartTime(properties, schedule.getLesson()))
                .endTime(calculateLessonEndTime(properties, schedule.getLesson()))
                .build();
    }

    private LocalTime calculateLessonStartTime(Map<String, Object> properties, int lessonNumber) {
        LocalTime dayStart = (LocalTime) properties.get("schoolDayStartTime");
        int lessonMinutes = (int) properties.get("lessonDurationMinutes");
        int shortBreak = (int) properties.get("shortBreakDurationMinutes");
        int longBreak = (int) properties.get("longBreakDurationMinutes");
        int longBreakAfter = (int) properties.get("longBreakAfterLessons");
        int totalMinutes = (lessonNumber - 1) * lessonMinutes + (lessonNumber - 1) / longBreakAfter * longBreak + (lessonNumber - 1 - (lessonNumber - 1) / longBreakAfter) * shortBreak;

        return dayStart.plusMinutes(totalMinutes);
    }

    private LocalTime calculateLessonEndTime(Map<String, Object> properties, int lessonNumber) {
        LocalTime startTime = calculateLessonStartTime(properties, lessonNumber);
        int lessonDurationMinutes = (int) properties.get("lessonDurationMinutes");
        return startTime.plusMinutes(lessonDurationMinutes);
    }
}