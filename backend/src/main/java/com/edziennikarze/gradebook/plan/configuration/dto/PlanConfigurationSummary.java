package com.edziennikarze.gradebook.plan.configuration.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PlanConfigurationSummary {

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
    private String officeWorkerName;

    @NotNull
    private String officeWorkerSurname;

}
