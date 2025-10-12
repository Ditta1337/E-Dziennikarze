package com.edziennikarze.gradebook.plan;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.edziennikarze.gradebook.plan.Plan;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;

import com.edziennikarze.gradebook.user.Role;


@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    public Mono<Plan> initializePlan(Mono<Plan> planMono) {
        Mono<Plan> enrichedPlan = planMono
                .flatMap(this::enrichPlanWithUniqueGroupCombinations)
                .flatMap(this::enrichPlanWithTeacherUnavailabilities);

        // TODO: add plan request to queue

        return enrichedPlan;
    }
   
    private Mono<Plan> enrichPlanWithUniqueGroupCombinations(Plan plan) {
        return userRepository.findAllByRole(Role.STUDENT)
                .flatMap(student -> studentGroupRepository.findAllByStudentId(student.getId())
                        .map(StudentGroup::getGroupId)
                        .sort()
                        .collectList())
                .collect(Collectors.toList())
                .map(uniqueCombinations -> {
                    plan.setUniqueGroupCombinations(uniqueCombinations);
                    return plan;
                });
    }
    
    public Mono<Plan> enrichPlanWithTeacherUnavailabilities(Plan plan) {
        return TeacherUnavailabilityRepository.getTeachers()
          
        }
}
