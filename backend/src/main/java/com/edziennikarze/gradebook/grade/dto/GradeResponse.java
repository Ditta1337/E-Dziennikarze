package com.edziennikarze.gradebook.grade.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("grades")
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
    @Builder.Default
    private Double weight = 1.0;

    @NotNull
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public static GradeResponse from(Grade grade, String subjectName) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .subjectId(grade.getSubjectId())
                .subjectName(subjectName)
                .grade(grade.getGrade())
                .weight(grade.getWeight())
                .createdAt(grade.getCreatedAt())
                .build();
    }
}
