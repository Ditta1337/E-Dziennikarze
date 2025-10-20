package com.edziennikarze.gradebook.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @Builder.Default
    private UUID planId = UUID.randomUUID();

    private int lessonsPerDay;

    private int latestStartingLesson;

    @NotNull
    private List<PlanGoal> goals;

    @NotNull
    private List<PlanGroup> groups;

    @Builder.Default
    private List<UUID> rooms = new ArrayList<>();

    @Builder.Default
    private List<List<UUID>> uniqueGroupCombinations = new ArrayList<>();

    @Builder.Default
    private List<PlanTeacher> teachers = new ArrayList<>();
}
