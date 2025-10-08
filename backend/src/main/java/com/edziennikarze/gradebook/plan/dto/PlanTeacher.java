package com.edziennikarze.gradebook.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlanTeacher {

    @NotNull
    private UUID teacherId;

    @NotNull
    @Builder.Default
    private List<PlanUnavailability> unavailability = new ArrayList<>();
}
