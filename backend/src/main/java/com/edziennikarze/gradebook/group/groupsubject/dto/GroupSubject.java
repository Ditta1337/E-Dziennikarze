package com.edziennikarze.gradebook.group.groupsubject.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("group_subjects")
public class GroupSubject {

    @Id
    private UUID id;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID groupId;

    @NotNull
    private UUID teacherId;

    private int lessonsPerWeek;

    private int maxLessonsPerDay;

    private boolean active;
}
