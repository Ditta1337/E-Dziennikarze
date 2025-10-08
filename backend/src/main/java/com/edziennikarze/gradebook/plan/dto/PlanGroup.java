package com.edziennikarze.gradebook.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlanGroup {

    @NotNull
    private UUID id;

    @NotNull
    @Builder.Default
    private List<UUID> conflictingGroups = new ArrayList<>();

    @NotNull
    private List<PlanSubject> subjects;
}
