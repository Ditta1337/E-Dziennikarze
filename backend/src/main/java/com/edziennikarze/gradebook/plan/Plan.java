package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.plan.dto.PlanGoal;
import com.edziennikarze.gradebook.plan.dto.PlanGroup;
import com.edziennikarze.gradebook.plan.dto.PlanTeacher;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Plan {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private int lessonsPerDay;

    @NotNull
    private List<PlanGoal> goals;

    @NotNull
    private List<PlanGroup> groups;

    @NotNull
    private List<List<UUID>> uniqueGroupCombinations;

    @NotNull
    private List<PlanTeacher> teachers;
}
