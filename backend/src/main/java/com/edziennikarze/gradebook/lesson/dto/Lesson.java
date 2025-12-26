package com.edziennikarze.gradebook.lesson.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    private UUID assignedLessonId;

    private UUID plannedLessonId;

    @NotNull
    private UUID groupId;

    @NotNull
    private String groupCode;

    @NotNull
    private LocalDate date;

    private boolean cancelled;

    private boolean modified;

    @NotNull
    private String subjectId;

    @NotNull
    private String subject;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private DayOfWeek weekDay;

    @NotNull
    private UUID roomId;

    @NotNull
    private String room;

    @NotNull
    private UUID teacherId;
}

