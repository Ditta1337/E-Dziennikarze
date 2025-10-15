package com.edziennikarze.gradebook.plan.configuration.dto;

import com.edziennikarze.gradebook.plan.dto.Plan;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PlanConfigurationResponse {

    @NotNull
    private UUID id;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private String name;

    @NotNull
    private UUID officeWorkerId;

    @NotNull
    private boolean calculated;

    @NotNull
    private Plan configuration;

    public static PlanConfigurationResponse from(PlanConfiguration planConfiguration, ObjectMapper objectMapper) {
        return PlanConfigurationResponse.builder()
                .id(planConfiguration.getId())
                .createdAt(planConfiguration.getCreatedAt())
                .name(planConfiguration.getName())
                .officeWorkerId(planConfiguration.getOfficeWorkerId())
                .configuration(planConfiguration.getConfigurationObject(objectMapper))
                .calculated(planConfiguration.isCalculated())
                .build();
    }

}
