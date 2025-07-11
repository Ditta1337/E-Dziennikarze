package com.edziennikarze.gradebook.group.studentgroup;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("students_groups")
public class StudentGroup {

    @Id
    private UUID id;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID groupId;
}
