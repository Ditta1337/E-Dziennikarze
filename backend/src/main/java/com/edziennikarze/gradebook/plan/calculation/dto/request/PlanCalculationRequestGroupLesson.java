package com.edziennikarze.gradebook.plan.calculation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanCalculationRequestGroupLesson {

    private int lesson;

    private int day;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID teacherId;

    @NotNull
    private UUID roomId;
}
