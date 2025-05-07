package com.edziennikarze.gradebook.user.student.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentsGuardianDTO {
    private final UUID studentId;
    private final UUID guardianId;
}
