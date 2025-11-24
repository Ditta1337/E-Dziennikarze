package com.edziennikarze.gradebook.plan.manual;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.MarshallException;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLessonResponse;
import com.edziennikarze.gradebook.plan.calculation.PlanCalculationRepository;
import com.edziennikarze.gradebook.plan.manual.dto.ManualPlan;
import com.edziennikarze.gradebook.plan.manual.dto.ManualPlanResponse;
import com.edziennikarze.gradebook.plan.manual.dto.ManualPlanSummary;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.teacher.TeacherRepository;
import com.edziennikarze.gradebook.user.teacher.dto.TeacherSubjectRow;
import com.edziennikarze.gradebook.util.PlannedLessonEnricher;
import com.edziennikarze.gradebook.util.TeacherResponseAssembler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

import static com.edziennikarze.gradebook.plan.manual.util.PlannedLessonsVerifier.verifyAndCreateErrors;

@Service
@RequiredArgsConstructor
public class ManualPlanService {

    private final ManualPlanRepository manualPlanRepository;

    private final PlanCalculationRepository planCalculationRepository;

    private final SubjectRepository subjectRepository;

    private final RoomRepository roomRepository;

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final TeacherRepository teacherRepository;

    private final LoggedInUserService loggedInUserService;

    private final ObjectMapper objectMapper;

    public Mono<UUID> copyCalculationToManualPlan(UUID id, String name) {
        return planCalculationRepository.findById(id)
                .flatMap(planCalculation -> {
                    ManualPlan manualPlan = new ManualPlan();
                    manualPlan.setName(name);
                    manualPlan.setPlanCalculationId(id);
                    manualPlan.setPlan(makePlanWithLessonsUUIDs(planCalculation.getCalculation()));

                    return loggedInUserService.getLoggedInUser()
                            .map(user -> {
                                manualPlan.setOfficeWorkerId(user.getId());
                                return manualPlan;
                            })
                            .flatMap(manualPlanRepository::save);
                })
                .map(ManualPlan::getId);
    }

    public Mono<ManualPlanResponse> getManualPlan(UUID id) {
        return manualPlanRepository.findById(id)
                .flatMap(this::createManualPlanDisplayResponse);
    }

    public Mono<ManualPlanResponse> saveManualPlan(UUID id, List<PlannedLessonResponse> manualPlan) {
        return manualPlanRepository.findById(id)
                .flatMap(existingPlan -> {
                    List<PlannedLesson> plannedLessons = manualPlan.stream()
                            .map(PlannedLesson::from)
                            .toList();
                    List<String> errors = verifyAndCreateErrors(manualPlan);
                    existingPlan.setPlan(plannedLessons, objectMapper);
                    existingPlan.setErrors(errors, objectMapper);
                    return manualPlanRepository.save(existingPlan)
                            .flatMap(this::createManualPlanDisplayResponse);
                });
    }

    public Flux<ManualPlanSummary> getManualPlanSummary() {
        return manualPlanRepository.findAllSummary();
    }

    public Mono<UUID> createEmptyManualPlan(String name) {
        return loggedInUserService.getLoggedInUser()
                .flatMap(user -> {
                    ManualPlan manualPlan = new ManualPlan();
                    manualPlan.setName(name);
                    manualPlan.setOfficeWorkerId(user.getId());
                    manualPlan.setPlan(List.of(), objectMapper);
                    return manualPlanRepository.save(manualPlan);
                }).map(ManualPlan::getId);
    }

    private Mono<ManualPlanResponse> createManualPlanDisplayResponse(ManualPlan manualPlan) {
        ManualPlanResponse manualPlanResponse = new ManualPlanResponse();
        manualPlanResponse.setName(manualPlan.getName());
        manualPlanResponse.setOfficeWorkerId(manualPlan.getOfficeWorkerId());
        manualPlanResponse.setPlanCalculationId(manualPlan.getPlanCalculationId());
        manualPlanResponse.setCreatedAt(manualPlan.getCreatedAt());
        manualPlanResponse.setErrors(manualPlan.getErrors(objectMapper));
        List<PlannedLessonResponse> plannedLessonResponses = createPlannedLessonResponses(manualPlan.getPlan(objectMapper));
        manualPlanResponse.setLessons(plannedLessonResponses);
        return teacherRepository.findAllActiveTeachersWithSubjects()
                .collectList()
                .flatMap(rows -> setTeachersToResponse(rows, manualPlanResponse))
                .then(enrichManualPlanSubjects(manualPlanResponse))
                .then(enrichManualPlanRooms(manualPlanResponse))
                .then(enrichManualPlanGroups(manualPlanResponse))
                .then(enrichOfficeWorkerData(manualPlanResponse))
                .then(enrichLessonsTeachers(plannedLessonResponses))
                .then(Mono.just(manualPlanResponse));
    }

    private Mono<Void> setTeachersToResponse(List<TeacherSubjectRow> rows, ManualPlanResponse response) {
        return TeacherResponseAssembler.createTeacherResponseList(rows)
                .collectList()
                .flatMap(teacherResponses -> {
                    response.setTeachers(teacherResponses);
                    return Mono.empty();
                });
    }

    private Mono<Void> enrichManualPlanSubjects(ManualPlanResponse response) {
        return subjectRepository.findAll()
                .collectList()
                .flatMap(subjects -> {
                    response.setSubjects(subjects);
                    return PlannedLessonEnricher.setLessonsSubjects(response.getLessons(), subjects);
                });
    }

    private Mono<Void> enrichManualPlanRooms(ManualPlanResponse response) {
        return roomRepository.findAll()
                .collectList()
                .flatMap(rooms -> {
                    response.setRooms(rooms);
                    return PlannedLessonEnricher.setLessonsRooms(response.getLessons(), rooms);
                });
    }

    private Mono<Void> enrichManualPlanGroups(ManualPlanResponse response) {
        return groupRepository.findAll()
                .collectList()
                .flatMap(groups -> {
                    response.setGroups(groups);
                    return PlannedLessonEnricher.setLessonsGroups(response.getLessons(), groups);
                });
    }

    private Mono<Void> enrichLessonsTeachers(List<PlannedLessonResponse> plannedLessonResponses) {
        return userRepository.findAllByRole(Role.TEACHER)
                .collectList()
                .flatMap(teachers -> PlannedLessonEnricher.setLessonsTeachers(plannedLessonResponses, teachers));
    }

    private Mono<Void> enrichOfficeWorkerData(ManualPlanResponse response) {
        return userRepository.findById(response.getOfficeWorkerId())
                .doOnNext(user -> {
                    response.setOfficeWorkerName(user.getName());
                    response.setOfficeWorkerSurname(user.getSurname());
                })
                .then();
    }


    private List<PlannedLessonResponse> createPlannedLessonResponses(List<PlannedLesson> plannedLessons) {
        return plannedLessons.stream().map(PlannedLessonResponse::from).collect(Collectors.toList());

    }

    private String makePlanWithLessonsUUIDs(String plan) {
        try {
            List<PlannedLesson> plannedLessons = objectMapper.readValue(plan, new TypeReference<>() {
            });
            plannedLessons.forEach(plannedLesson -> plannedLesson.setId(UUID.randomUUID()));
            return objectMapper.writeValueAsString(plannedLessons);
        } catch (JsonProcessingException e) {
            throw new MarshallException("Failed to convert PlannedLessons to JSON string");
        }
    }

}
