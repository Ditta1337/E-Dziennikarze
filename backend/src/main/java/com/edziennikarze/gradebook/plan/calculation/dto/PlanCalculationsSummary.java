package com.edziennikarze.gradebook.plan.calculation.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanCalculationsSummary {

    @NotNull
    private String id;

    @NotNull
    private String planName;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private String goals;
}
