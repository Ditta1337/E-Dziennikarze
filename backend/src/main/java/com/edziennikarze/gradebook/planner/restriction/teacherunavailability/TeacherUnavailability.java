package com.edziennikarze.gradebook.planner.restriction.teacherunavailability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("teacher_unavailabilities")
public class TeacherUnavailability {

    @Id
    private UUID id;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private DayOfWeek weekDay;

    @NotNull
    private UUID teacherId;
}
