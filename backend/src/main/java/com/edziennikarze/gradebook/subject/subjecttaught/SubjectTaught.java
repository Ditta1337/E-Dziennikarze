package com.edziennikarze.gradebook.subject.subjecttaught;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@Table("subjects_taught")
public class SubjectTaught {
    @Id
    private UUID id;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID teacherId;
}

