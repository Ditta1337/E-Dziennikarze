package com.edziennikarze.gradebook.user.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class TeacherSubjectsTaughtResponse {

    private UUID subjectId;

    private String subjectName;

}