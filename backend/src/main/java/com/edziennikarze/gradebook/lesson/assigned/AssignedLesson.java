package com.edziennikarze.gradebook.lesson.assigned;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("assigned_lessons")
public class AssignedLesson {

    @Id
    private UUID id;

    @NotNull
    private UUID plannedLessonId;

    @NotNull
    private LocalDate date;

    @Builder.Default
    private boolean cancelled = false;

    @Builder.Default
    private boolean modified = false;
}
