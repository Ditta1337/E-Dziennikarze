package com.edziennikarze.gradebook.group.teachergroup;

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
