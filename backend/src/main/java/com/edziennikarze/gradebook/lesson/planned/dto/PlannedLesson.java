package com.edziennikarze.gradebook.lesson.planned.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private boolean active = true;

    @NotNull
    private DayOfWeek weekDay;

    @NotNull
    private UUID roomId;

    @NotNull
    private UUID groupId;

    @NotNull
    private UUID teacherId;

    public static PlannedLesson from(PlannedLessonResponse response) {
        return PlannedLesson.builder()
                .id(response.getId())
                .subjectId(response.getSubjectId())
                .startTime(response.getStartTime())
                .endTime(response.getEndTime())
                .active(response.isActive())
                .weekDay(response.getWeekDay())
                .roomId(response.getRoomId())
                .groupId(response.getGroupId())
                .teacherId(response.getTeacherId())
                .build();
    }

}
