package com.edziennikarze.gradebook.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @Builder.Default
    private UUID planId = UUID.randomUUID();

    @NotNull
    private String name;

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
