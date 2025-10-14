package com.edziennikarze.gradebook.grade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GradeResponse {

    @Id
    private UUID id;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private String subjectName;

    @NotNull
    private Double grade;

    @NotNull
    private Double weight;

    private boolean isFinal;

    @NotNull
    private LocalDateTime createdAt;

    public static GradeResponse from(Grade grade, String subjectName) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .subjectId(grade.getSubjectId())
                .subjectName(subjectName)
                .grade(grade.getGrade())
                .weight(grade.getWeight())
                .isFinal(grade.isFinal())
                .createdAt(grade.getCreatedAt())
                .build();
    }
}
