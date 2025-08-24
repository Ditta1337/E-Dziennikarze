package com.edziennikarze.gradebook.utils;

import com.edziennikarze.gradebook.attendance.Attendance;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.room.Room;
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

    public static Group buildGroup(int startYear, String groupCode, boolean isClass) {
        return Group.builder()
                .startYear(startYear)
                .groupCode(groupCode)
                .isClass(isClass)
                .build();
    }

    public static StudentGroup buildStudentGroup(UUID studentId, UUID groupId) {
        return StudentGroup.builder()
                .studentId(studentId)
                .groupId(groupId)
                .build();
    }

    public static Room buildRoom(int capacity, String roomCode) {
        return Room.builder()
                .capacity(capacity)
                .roomCode(roomCode)
                .build();
    }

    public static Attendance buildAttendance(UUID studentId, UUID subjectId, UUID lessonId, boolean present) {
        return Attendance.builder()
                .studentId(studentId)
                .subjectId(subjectId)
                .lessonId(lessonId)
                .present(present)
                .build();
    }
}