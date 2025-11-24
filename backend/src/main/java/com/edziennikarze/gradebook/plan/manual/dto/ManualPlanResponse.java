package com.edziennikarze.gradebook.plan.manual.dto;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLessonResponse;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.user.teacher.dto.TeacherResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPlanResponse {

    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private UUID officeWorkerId;

    @NotNull
    private String officeWorkerName;

    @NotNull
    private String officeWorkerSurname;

    private UUID planCalculationId;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    List<Subject> subjects;

    @NotNull
    List<Room> rooms;

    @NotNull
    List<Group> groups;

    @NotNull
    List<TeacherResponse> teachers;

    @NotNull
    List<PlannedLessonResponse> lessons;

    List<String> errors;

}
