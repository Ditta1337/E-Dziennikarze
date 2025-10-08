package com.edziennikarze.gradebook.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PlanRoom {

    @NotNull
    private List<UUID> allowed;

    @NotNull
    private List<UUID> preferred;

    @NotNull
    private List<UUID> dispreferred;
}
