package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final GroupRepository groupRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    public Mono<Plan> initializePlan(Mono<Plan> planMono) {
        Mono<Plan> enrichedPlan = planMono
                .flatMap(this::enrichPlanWithConflictingGroups)
                .flatMap(this::enrichPlanWithTeacherUnavailabilities);

        // TODO: add plan request to queue

        return enrichedPlan;
    }

    private Mono<Plan> enrichPlanWithConflictingGroups(Plan plan) {
        // TODO szywoj
        return Mono.just(plan);
    }

    private Mono<Plan> enrichPlanWithTeacherUnavailabilities(Plan plan) {
        // TODO szywoj
        return Mono.just(plan);
    }
}
