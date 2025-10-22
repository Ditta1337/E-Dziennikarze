package com.edziennikarze.gradebook.plan.calculation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
