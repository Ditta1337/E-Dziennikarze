package com.edziennikarze.gradebook.plan.manual.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPlanSummary {

    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String officeWorkerName;

    @NotNull
    private String officeWorkerSurname;

    private UUID planCalculationId;

    @NotNull
    private LocalDateTime createdAt;

}
