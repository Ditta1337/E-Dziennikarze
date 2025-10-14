package com.edziennikarze.gradebook.grade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GradeAverageResponse {

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private Double average;
}
