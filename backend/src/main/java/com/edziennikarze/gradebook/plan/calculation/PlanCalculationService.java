package com.edziennikarze.gradebook.plan.calculation;

import com.edziennikarze.gradebook.group.groupsubject.GroupSubjectRepository;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculation;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculationsSummary;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculationsSummaryResponse;
import com.edziennikarze.gradebook.plan.calculation.dto.request.*;
import com.edziennikarze.gradebook.plan.configuration.PlanConfigurationRepository;
import com.edziennikarze.gradebook.property.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PlanCalculationService {

    private final PlanCalculationRepository planCalculationRepository;

    private final PlanConfigurationRepository planConfigurationRepository;

    private final GroupSubjectRepository groupSubjectRepository;

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
        return planCalculationRequestMono.flatMap(planCalculationRequest -> planConfigurationRepository.updateCalculatedStatus(planCalculationRequest.getPlanId(), true)
                .then(mapToPlanCalculation(planCalculationRequest))
        );
    }

    public Mono<PlanCalculationResponse> getAllPlanCalculationsForPlan(UUID id) {
        return planCalculationRepository.findById(id)
                .map(planCalculation -> PlanCalculationResponse.from(planCalculation, objectMapper));
    }

    public Flux<PlanCalculationsSummaryResponse> getPlanCalculationsSummary(UUID planId) {
        return planCalculationRepository.findAllSummaryByPlanId(planId)
                .map(summary -> PlanCalculationsSummaryResponse.from(summary, objectMapper));
    }

    private Mono<PlanCalculationResponse> mapToPlanCalculation(PlanCalculationRequest request) {
        return propertyService.getPropertiesAsMap(LESSON_PROPERTIES_NAMES)
                .flatMap(properties ->
                        mapGroupSubjectIdsToSubjectIds(request)
                                .flatMap(req -> {
                                    List<PlannedLesson> plannedLessons = generatePlannedLessons(req, properties);

                                    PlanCalculation planCalculation = new PlanCalculation();
                                    planCalculation.setName(req.getName());
                                    planCalculation.setPlanId(req.getPlanId());
                                    planCalculation.setCalculation(plannedLessons, objectMapper);
                                    planCalculation.setGoals(req.getGoals(), objectMapper);


                                    return planCalculationRepository.save(planCalculation);
                                })
                                .map(savedPlan -> PlanCalculationResponse.from(savedPlan, objectMapper))
                );
    }

    private Mono<PlanCalculationRequest> mapGroupSubjectIdsToSubjectIds(PlanCalculationRequest request) {
        Flux<Void> allUpdateOperations = Flux.fromIterable(request.getTeachers())
                .flatMap(teacher ->
                        Flux.fromIterable(teacher.getSchedule())
                                .flatMap(schedule ->
                                        groupSubjectRepository.findById(schedule.getSubjectId())
                                                .map(GroupSubject::getSubjectId)
                                                .doOnNext(schedule::setSubjectId)
                                                .then()
                                )
                );

        return allUpdateOperations.then(Mono.just(request));
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
        int totalMinutes = 0;
        for (int i = 0; i < lessonNumber; i++) {
            totalMinutes += lessonMinutes;
            if (i == longBreakAfter) {
                totalMinutes += longBreak;
            } else {
                totalMinutes += shortBreak;
            }
        }

        return dayStart.plusMinutes(totalMinutes);
    }

    private LocalTime calculateLessonEndTime(Map<String, Object> properties, int lessonNumber) {
        LocalTime startTime = calculateLessonStartTime(properties, lessonNumber);
        int lessonDurationMinutes = (int) properties.get("lessonDurationMinutes");
        return startTime.plusMinutes(lessonDurationMinutes);
    }
}