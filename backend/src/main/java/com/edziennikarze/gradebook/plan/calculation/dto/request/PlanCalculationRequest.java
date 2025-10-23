package com.edziennikarze.gradebook.plan.calculation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanCalculationRequest {

    @NotNull
    private UUID planId;

    @NotNull
    private String name;

    @NotNull
    private List<PlanCalculationRequestGoal> goals;

    @NotNull
    private List<PlanCalculationRequestGroup> groups;

    @NotNull
    private List<PlanCalculationRequestTeacher> teachers;
}
