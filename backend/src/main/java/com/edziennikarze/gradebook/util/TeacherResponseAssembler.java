package com.edziennikarze.gradebook.util;

import com.edziennikarze.gradebook.user.teacher.dto.TeacherResponse;
import com.edziennikarze.gradebook.user.teacher.dto.TeacherSubjectRow;
import com.edziennikarze.gradebook.user.teacher.dto.TeacherSubjectsTaughtResponse;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TeacherResponseAssembler {

    public static Flux<TeacherResponse> createTeacherResponseList(List<TeacherSubjectRow> teacherSubjectRows) {
        if (teacherSubjectRows == null || teacherSubjectRows.isEmpty()) {
            return Flux.empty();
        }

        List<TeacherResponse> teacherResponses = new ArrayList<>();

        TeacherResponse.TeacherResponseBuilder currentTeacherBuilder = null;
        List<TeacherSubjectsTaughtResponse> currentSubjects = new ArrayList<>();
        UUID lastTeacherId = null;

        for (TeacherSubjectRow row : teacherSubjectRows) {
            UUID currentTeacherId = row.getTeacherId();

            if (!currentTeacherId.equals(lastTeacherId)) {
                if (currentTeacherBuilder != null) {
                    teacherResponses.add(currentTeacherBuilder
                            .subjectsTaught(currentSubjects)
                            .build());
                }
                currentTeacherBuilder = TeacherResponse.builder()
                        .id(row.getTeacherId())
                        .name(row.getTeacherName())
                        .surname(row.getTeacherSurname())
                        .createdAt(row.getCreatedAt())
                        .address(row.getAddress())
                        .email(row.getEmail())
                        .contact(row.getContact())
                        .imageBase64(row.getImageBase64());

                currentSubjects = new ArrayList<>();
                lastTeacherId = currentTeacherId;
            }

            if (row.getSubjectId() != null) {
                TeacherSubjectsTaughtResponse subject = new TeacherSubjectsTaughtResponse(
                        row.getSubjectId(),
                        row.getSubjectName()
                );
                currentSubjects.add(subject);
            }
        }

        if (currentTeacherBuilder != null) {
            teacherResponses.add(currentTeacherBuilder
                    .subjectsTaught(currentSubjects)
                    .build());
        }

        return Flux.fromIterable(teacherResponses);
    }

}
