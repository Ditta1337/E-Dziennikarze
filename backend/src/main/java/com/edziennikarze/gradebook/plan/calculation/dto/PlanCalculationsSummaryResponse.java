package com.edziennikarze.gradebook.plan.calculation.dto;

import com.edziennikarze.gradebook.plan.calculation.dto.request.PlanCalculationRequestGoal;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanCalculationsSummaryResponse {

    @NotNull
    private UUID id;

    @NotNull
    private String planName;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private List<PlanCalculationRequestGoal> goals;

    public static PlanCalculationsSummaryResponse from(PlanCalculationsSummary summary, ObjectMapper objectMapper) {
        List<PlanCalculationRequestGoal> goalsList;
        try {
            goalsList = objectMapper.readValue(summary.getGoals(), new TypeReference<List<PlanCalculationRequestGoal>>() {});
        } catch (Exception e) {
            goalsList = Collections.emptyList();
        }

        return PlanCalculationsSummaryResponse.builder()
                .id(UUID.fromString(summary.getId()))
                .planName(summary.getPlanName())
                .createdAt(summary.getCreatedAt())
                .goals(goalsList)
                .build();
    }

}
