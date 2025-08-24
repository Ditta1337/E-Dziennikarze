package com.edziennikarze.gradebook.lesson.planned;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("planned_lessons")
public class PlannedLesson {

    @Id
    private UUID id;

    @NotNull
    private UUID subjectId;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private boolean active;

    @NotNull
    private DayOfWeek weekDay;

    @NotNull
    private UUID roomId;

    @NotNull
    private UUID groupId;

    @NotNull
    private UUID teacherId;
}
