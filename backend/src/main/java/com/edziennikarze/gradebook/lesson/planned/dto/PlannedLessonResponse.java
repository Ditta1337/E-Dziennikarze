package com.edziennikarze.gradebook.lesson.planned.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedLessonResponse {

    @NotNull
    private UUID id;

    @NotNull
    private UUID subjectId;

    private String subject;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private boolean active;

    @NotNull
    private DayOfWeek weekDay;

    @NotNull
    private UUID roomId;

    private String room;

    @NotNull
    private UUID groupId;

    private String group;

    @NotNull
    private UUID teacherId;

    private String teacher;

    public static PlannedLessonResponse from(PlannedLesson lesson) {
        return PlannedLessonResponse.builder()
                .id(lesson.getId())
                .subjectId(lesson.getSubjectId())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .active(lesson.isActive())
                .weekDay(lesson.getWeekDay())
                .roomId(lesson.getRoomId())
                .groupId(lesson.getGroupId())
                .teacherId(lesson.getTeacherId())
                .build();
    }

}
