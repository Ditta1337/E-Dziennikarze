package com.edziennikarze.gradebook.plan.configuration;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.group.groupsubject.GroupSubjectRepository;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubjectResponse;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfiguration;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationResponse;
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

    public Mono<PlanConfiguration> createPlanConfiguration(Mono<String> nameMono) {
        return nameMono.flatMap(name ->
            createEmptyPlan()
                    .flatMap(plan ->
                            loggedInUserService.getLoggedInUser()
                                    .flatMap(user -> {
                                                PlanConfiguration planConfiguration = PlanConfiguration.builder()
                                                        .name(name)
                                                        .officeWorkerId(user.getId())
                                                        .calculated(false)
                                                        .build();
                                                planConfiguration.setConfigurationObject(plan, objectMapper);

                                                return planConfigurationRepository.save(planConfiguration);
                                            }
                                    )
                    )
        );
    }

    public Mono<PlanConfigurationResponse> getPlanConfiguration(UUID planConfigurationId) {
        return planConfigurationRepository.findById(planConfigurationId)
                .map(planConfiguration -> PlanConfigurationResponse.from(planConfiguration, objectMapper));
    }


    private Mono<Plan> createEmptyPlan() {
        return createGroupToGroupSubjectsTuple()
                .map(this::createPlanGroup)
                .collectList()
                .map(groups -> Plan.builder()
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
