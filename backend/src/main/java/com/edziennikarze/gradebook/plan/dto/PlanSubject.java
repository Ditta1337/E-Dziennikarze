package com.edziennikarze.gradebook.plan.dto;

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
public class PlanSubject {

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID teacherId;

    private int lessonsPerWeek;

    private int maxLessonsPerDay;

    @NotNull
    private String type;

    private PlanRoom room;
}
