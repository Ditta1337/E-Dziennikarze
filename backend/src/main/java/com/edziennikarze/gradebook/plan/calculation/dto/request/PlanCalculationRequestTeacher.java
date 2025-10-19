package com.edziennikarze.gradebook.plan.calculation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanCalculationRequestTeacher {

    @NotNull
    private UUID teacherId;

    @NotNull
    private List<PlanCalculationRequestTeacherLesson> schedule;
}
