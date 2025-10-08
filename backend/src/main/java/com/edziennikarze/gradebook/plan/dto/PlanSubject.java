package com.edziennikarze.gradebook.plan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
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
