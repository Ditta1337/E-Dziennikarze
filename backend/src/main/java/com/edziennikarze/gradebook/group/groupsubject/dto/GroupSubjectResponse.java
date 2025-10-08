package com.edziennikarze.gradebook.group.groupsubject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GroupSubjectResponse {

    @NotNull
    private UUID id;

    @NotNull
    private UUID subjectId;

    @NotNull
    private String subjectName;

    @NotNull
    private UUID groupId;

    @NotNull
    private String groupCode;

    private int studentsInGroup;

    @NotNull
    private UUID teacherId;

    @NotNull
    private String teacherName;

    @NotNull
    private String teacherSurname;

    private int lessonsPerWeek;

    private int maxLessonsPerDay;

    private boolean active;
}
