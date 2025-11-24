package com.edziennikarze.gradebook.user.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class TeacherResponse {

    private UUID id;

    private String name;

    private String surname;

    private LocalDate createdAt;

    private String address;

    private String email;

    private String contact;

    private String imageBase64;

    private List<TeacherSubjectsTaughtResponse> subjectsTaught;

}
