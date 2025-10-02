package com.edziennikarze.gradebook.group.studentgroup;

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
@Table("student_groups")
public class StudentGroup {

    @Id
    private UUID id;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID groupId;
}
