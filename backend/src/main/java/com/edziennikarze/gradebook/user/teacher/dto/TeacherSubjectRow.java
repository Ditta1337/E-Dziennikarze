package com.edziennikarze.gradebook.user.teacher.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TeacherSubjectRow {

    private final UUID teacherId;

    private final String teacherName;

    private final String teacherSurname;

    private final LocalDate createdAt;

    private final String address;

    private final String email;

    private final String contact;

    private final String imageBase64;

    private final UUID subjectId;

    private final String subjectName;

}

