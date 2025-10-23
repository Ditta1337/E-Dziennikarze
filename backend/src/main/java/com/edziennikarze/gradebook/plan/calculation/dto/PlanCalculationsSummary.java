package com.edziennikarze.gradebook.plan.calculation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanCalculationsSummary {

    @NotNull
    private UUID id;

    @NotNull
    private String planName;

    @NotNull
    private LocalDateTime createdAt;

}
