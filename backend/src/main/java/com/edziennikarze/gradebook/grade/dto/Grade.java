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
public class Grade {

    @Id
    private UUID id;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private Double grade;

    @Builder.Default
    private Double weight = 1.0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
