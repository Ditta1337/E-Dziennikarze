package com.edziennikarze.gradebook.utils;

import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaught;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;

import java.time.LocalDate;
import java.util.UUID;

public class TestObjectBuilder {

    public static User buildUser(String email, Role role, boolean isActive, boolean isChoosingPreferences) {
        String namePart = email.split("@")[0];
        return User.builder()
                .name(namePart + "'s Name")
                .surname(namePart + "'s Surname")
                .createdAt(LocalDate.now())
                .address(namePart + "'s Address")
                .email(email)
                .password("somePassword")
                .role(role)
                .contact("123456789")
                .imageBase64("someImageBase64")
                .isActive(isActive)
                .isChoosingPreferences(isChoosingPreferences)
                .build();
    }

    public static Subject buildSubject(String name) {
        return Subject.builder()
                .name(name)
                .build();
    }

    public static SubjectTaught buildSubjectTaught(UUID teacherId, UUID subjectId) {
        return SubjectTaught.builder()
                .teacherId(teacherId)
                .subjectId(subjectId)
                .build();
    }
}
