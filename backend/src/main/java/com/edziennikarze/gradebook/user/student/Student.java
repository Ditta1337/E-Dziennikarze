package com.edziennikarze.gradebook.user.student;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("students")
@Builder
public class Student {
    @Id
    private UUID id;
    private UUID userId;
    private UUID guardianId;
    private boolean canChoosePreferences;
}
