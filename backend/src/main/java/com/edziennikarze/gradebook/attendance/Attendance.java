package com.edziennikarze.gradebook.attendance;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("attendances")
public class Attendance {

    @Id
    private UUID id;

    @NotNull
    private UUID lessonId;

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private AttendanceStatus status;
}
