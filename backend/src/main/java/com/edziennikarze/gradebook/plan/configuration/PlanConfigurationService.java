package com.edziennikarze.gradebook.plan.configuration;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.group.groupsubject.GroupSubjectRepository;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubjectResponse;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfiguration;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationResponse;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationSummary;
import com.edziennikarze.gradebook.plan.dto.Plan;
import com.edziennikarze.gradebook.plan.dto.PlanGroup;
import com.edziennikarze.gradebook.plan.dto.PlanRoom;
import com.edziennikarze.gradebook.plan.dto.PlanSubject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanConfigurationService {

    private static final String LESSON_PLACEMENT_TYPE_ANY = "ANY";

    private final GroupSubjectRepository groupSubjectRepository;

    private final LoggedInUserService loggedInUserService;

    private final PlanConfigurationRepository planConfigurationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<PlanConfigurationResponse> createPlanConfiguration(Mono<String> nameMono) {
        return Mono.just(new PlanConfiguration())
                .flatMap(this::enrichPlanConfigurationWithPlanId)
                .flatMap(planConfiguration -> enrichPlanConfigurationWithName(planConfiguration, nameMono))
                .flatMap(this::enrichPlanConfigurationWithOfficeWorker)
                .flatMap(this::enrichPlanConfigurationWithCalculated)
                .flatMap(this::enrichPlanConfigurationWithPlan)
                .flatMap(planConfigurationRepository::save)
                .flatMap(planConfiguration -> Mono.just(PlanConfigurationResponse.from(planConfiguration, objectMapper)));

    }

    private Mono<PlanConfiguration> enrichPlanConfigurationWithPlanId(PlanConfiguration planConfiguration) {
        planConfiguration.setPlanId(UUID.randomUUID());
        return Mono.just(planConfiguration);
    }

    private Mono<PlanConfiguration> enrichPlanConfigurationWithName(PlanConfiguration planConfiguration, Mono<String> nameMono) {
        return nameMono.flatMap(name -> {
            planConfiguration.setName(name);
            return Mono.just(planConfiguration);
        });
    }

    private Mono<PlanConfiguration> enrichPlanConfigurationWithOfficeWorker(PlanConfiguration planConfiguration) {
        return loggedInUserService.getLoggedInUser()
                .flatMap(user -> {
                    planConfiguration.setOfficeWorkerId(user.getId());
                    return Mono.just(planConfiguration);
                });
    }

    private Mono<PlanConfiguration> enrichPlanConfigurationWithCalculated(PlanConfiguration planConfiguration) {
        planConfiguration.setCalculated(false);
        return Mono.just(planConfiguration);
    }

    private Mono<PlanConfiguration> enrichPlanConfigurationWithPlan(PlanConfiguration planConfiguration) {
        return createEmptyPlan(planConfiguration.getPlanId())
                .flatMap(plan -> {
                    planConfiguration.setConfiguration(plan, objectMapper);
                    return Mono.just(planConfiguration);
                });
    }

    public Mono<PlanConfigurationResponse> getPlanConfiguration(UUID planConfigurationId) {
        return planConfigurationRepository.findById(planConfigurationId)
                .map(planConfiguration -> PlanConfigurationResponse.from(planConfiguration, objectMapper));
    }

    public Flux<PlanConfigurationSummary> getPlanConfigurationSummaryList() {
        return planConfigurationRepository.findAllSummary();
    }

    public Mono<PlanConfigurationResponse> updatePlanConfiguration(Mono<PlanConfigurationResponse> planConfigurationResponseMono) {
        return planConfigurationResponseMono.flatMap(planConfigurationResponse -> planConfigurationRepository.findById(planConfigurationResponse.getId())
                .flatMap(planConfiguration -> {
                    planConfiguration.setConfiguration(planConfigurationResponse.getConfiguration(), objectMapper);
                    return planConfigurationRepository.save(planConfiguration);
                }).flatMap(planConfiguration -> Mono.just(PlanConfigurationResponse.from(planConfiguration, objectMapper))));
    }


    private Mono<Plan> createEmptyPlan(UUID planId) {
        return createGroupToGroupSubjectsTuple()
                .map(this::createPlanGroup)
                .collectList()
                .map(groups -> Plan.builder()
                        .planId(planId)
                        .goals(List.of())
                        .groups(groups)
                        .teachers(List.of())
                        .rooms(List.of())
                        .build()
                );
    }

    private PlanGroup createPlanGroup(Tuple2<UUID, List<GroupSubjectResponse>> tuple) {
        List<PlanSubject> subjects = new ArrayList<>();
        for (GroupSubjectResponse groupSubjectResponse : tuple.getT2()) {
            subjects.add(createPlanSubject(groupSubjectResponse));
        }
        return PlanGroup.builder()
                .groupId(tuple.getT1())
                .subjects(subjects)
                .build();
    }

    private PlanSubject createPlanSubject(GroupSubjectResponse groupSubjectResponse) {
        return PlanSubject.builder()
                .subjectId(groupSubjectResponse.getSubjectId())
                .teacherId(groupSubjectResponse.getTeacherId())
                .room(createPlanRoom())
                .type(LESSON_PLACEMENT_TYPE_ANY)
                .build();
    }

    private PlanRoom createPlanRoom() {
        return PlanRoom.builder()
                .allowed(List.of())
                .preferred(List.of())
                .dispreferred(List.of())
                .build();
    }

    private Flux<Tuple2<UUID, List<GroupSubjectResponse>>> createGroupToGroupSubjectsTuple() {
        return groupSubjectRepository.findAllByActiveTrue()
                .collectMultimap(GroupSubjectResponse::getGroupId)
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> Tuples.of(entry.getKey(), new ArrayList<>(entry.getValue())));
    }

}
