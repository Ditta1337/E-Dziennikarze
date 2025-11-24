package com.edziennikarze.gradebook.subject.subjecttaught.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("subjects_taught")
public class SubjectTaught {
    @Id
    private UUID id;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID teacherId;
}

