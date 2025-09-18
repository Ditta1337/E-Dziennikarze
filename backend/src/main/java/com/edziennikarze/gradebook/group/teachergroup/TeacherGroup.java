package com.edziennikarze.gradebook.group.teachergroup;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("teacher_groups")
public class TeacherGroup {

    @Id
    private UUID id;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID groupId;

    @NotNull
    private UUID teacherId;
}
