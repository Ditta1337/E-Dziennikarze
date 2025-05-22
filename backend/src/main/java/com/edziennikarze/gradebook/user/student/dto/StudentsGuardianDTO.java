package com.edziennikarze.gradebook.user.student.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StudentsGuardianDTO {
    private final UUID studentId;
    private final UUID guardianId;
}
