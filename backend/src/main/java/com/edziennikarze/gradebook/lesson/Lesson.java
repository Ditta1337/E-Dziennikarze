package com.edziennikarze.gradebook.lesson;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Lesson {

    @NotNull
    private UUID assignedLessonId;

    @NotNull
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
    private String room;

    @NotNull
    private UUID teacherId;
}

