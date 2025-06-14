package com.edziennikarze.gradebook.user.student;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Table("students")
@Builder
public class Student {

    @Id
    private UUID id;

    @NotNull
    private UUID userId;

    private UUID guardianId;

    private boolean canChoosePreferences;
}
