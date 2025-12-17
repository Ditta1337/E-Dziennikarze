package com.edziennikarze.gradebook.lesson.assigned;

import com.edziennikarze.gradebook.lesson.assigned.dto.FillCalendarRequest;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import com.edziennikarze.gradebook.plan.calculation.PlanCalculationRepository;
import com.edziennikarze.gradebook.plan.manual.ManualPlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignedLessonService {

    private final AssignedLessonRepository assignedLessonRepository;

    private final PlannedLessonRepository plannedLessonRepository;

    private final ManualPlanRepository manualPlanRepository;

    private final PlanCalculationRepository planCalculationRepository;

    private final ObjectMapper objectMapper;

    public Mono<AssignedLesson> createAssignedLesson(Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonMono.flatMap(assignedLessonRepository::save);
    }

    public Flux<AssignedLesson> getAllAssignedLessons() {
        return assignedLessonRepository.findAll();
    }

    public Flux<AssignedLesson> getAllCancelledAssignedLessons() {
        return assignedLessonRepository.findAllByCancelled(true);
    }

    public Mono<AssignedLesson> updateAssignedLesson(Mono<AssignedLesson> assignedLessonMono) {
        return assignedLessonMono.flatMap(assignedLesson -> assignedLessonRepository.findById(assignedLesson.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Assigned lesson with id " + assignedLesson.getId() + " not found")))
                .flatMap(existingAssignedLesson -> {
                    existingAssignedLesson.setPlannedLessonId(assignedLesson.getPlannedLessonId());
                    existingAssignedLesson.setDate(assignedLesson.getDate());
                    existingAssignedLesson.setCancelled(assignedLesson.isCancelled());
                    existingAssignedLesson.setModified(assignedLesson.isModified());
                    return assignedLessonRepository.save(existingAssignedLesson);
                }));
    }

    public Mono<Void> fillManual(Mono<FillCalendarRequest> fillCalendarRequestMono) {
        return fillCalendarRequestMono.flatMap(request -> {
            if (request.getFrom().isAfter(request.getTo())) {
                return Mono.error(new IllegalArgumentException("From cannot be after to."));
            }
            return manualPlanRepository.findById(request.getId()).flatMap(manualPlan ->
                    plannedLessonRepository.saveAll(emptyPlannedLessonsIds(manualPlan.getPlan(objectMapper)))
                            .collectList()
                            .flatMap(plannedLessons -> {
                                List<AssignedLesson> assignedLessons = generateAssignedLessons(request, plannedLessons);
                                return assignedLessonRepository.saveAll(assignedLessons).then();
                            }));
        });
    }

    public Mono<Void> fillGenerated(Mono<FillCalendarRequest> fillCalendarRequestMono) {
        return fillCalendarRequestMono.flatMap(request -> {
            if (request.getFrom().isAfter(request.getTo())) {
                return Mono.error(new IllegalArgumentException("From cannot be after to."));
            }
            return planCalculationRepository.findById(request.getId()).flatMap(planCalculation ->
                    plannedLessonRepository.saveAll(emptyPlannedLessonsIds(planCalculation.getCalculation(objectMapper)))
                            .collectList()
                            .flatMap(plannedLessons -> {
                                List<AssignedLesson> assignedLessons = generateAssignedLessons(request, plannedLessons);
                                return assignedLessonRepository.saveAll(assignedLessons).then();
                            })
                    );
        });
    }

    private List<PlannedLesson> emptyPlannedLessonsIds(List<PlannedLesson> plannedLessons) {
        return plannedLessons.stream().map(plannedLesson -> PlannedLesson.builder()
                .subjectId(plannedLesson.getSubjectId())
                .startTime(plannedLesson.getStartTime())
                .endTime(plannedLesson.getEndTime())
                .active(plannedLesson.isActive())
                .weekDay(plannedLesson.getWeekDay())
                .roomId(plannedLesson.getRoomId())
                .groupId(plannedLesson.getGroupId())
                .teacherId(plannedLesson.getTeacherId())
                .build()
        ).toList();
    }


    private List<AssignedLesson> generateAssignedLessons(FillCalendarRequest request, List<PlannedLesson> plannedLessons) {
        List<LocalDate> dates = request.getFrom().datesUntil(request.getTo()).toList();
        List<AssignedLesson> assignedLessons = new ArrayList<>();
        plannedLessons.forEach(plannedLesson -> dates.stream().filter(date -> date.getDayOfWeek() == plannedLesson.getWeekDay())
                .forEach(date -> assignedLessons.add(AssignedLesson.builder()
                        .plannedLessonId(plannedLesson.getId())
                        .date(date)
                        .build())));
        return assignedLessons;
    }
}
