package com.edziennikarze.gradebook.plan.calculation.dto.request;

import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculation;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanCalculationResponse {

    @NotNull
    private UUID id;

    @NotNull
    private UUID planId;

    @NotNull
    private String name;

    @NotNull
    private LocalDateTime calculatedAt;

    @NotNull
    private List<PlannedLesson> calculation;

    public static PlanCalculationResponse from(PlanCalculation planCalculation, ObjectMapper objectMapper) {
        return PlanCalculationResponse.builder()
                .id(planCalculation.getId())
                .planId(planCalculation.getPlanId())
                .name(planCalculation.getName())
                .calculatedAt(planCalculation.getCreatedAt())
                .calculation(planCalculation.getCalculation(objectMapper))
                .build();
    }

}
