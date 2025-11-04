package com.edziennikarze.gradebook.plan.calculation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanCalculationRequestGoal {

    @NotNull
    private String name;

    private String title;

    private Map<String, Integer> value;
}
