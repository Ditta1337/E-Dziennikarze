package com.edziennikarze.gradebook.lesson;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Lesson {

    @NotNull
    private UUID id;

    @NotNull
    private PlannedLesson plannedLesson;

    @NotNull
    private LocalDate date;

    private boolean cancelled;

    private boolean modified;

    public static Lesson from(AssignedLesson assignedLesson, PlannedLesson plannedLesson) {
        return Lesson.builder()
                .id(assignedLesson.getId())
                .date(assignedLesson.getDate())
                .cancelled(assignedLesson.isCancelled())
                .modified(assignedLesson.isCancelled())
                .plannedLesson(plannedLesson)
                .build();
    }
}
