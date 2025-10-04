package com.edziennikarze.gradebook.util;

import com.edziennikarze.gradebook.attendance.Attendance;
import com.edziennikarze.gradebook.attendance.AttendanceStatus;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroup;
import com.edziennikarze.gradebook.group.teachergroup.TeacherGroup;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLesson;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
import com.edziennikarze.gradebook.planner.restriction.teacherunavailability.TeacherUnavailability;
import com.edziennikarze.gradebook.property.Property;
import com.edziennikarze.gradebook.property.PropertyType;
import com.edziennikarze.gradebook.room.Room;
import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaught;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.studentguardian.StudentGuardian;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public final class ObjectsBuilder {

    private ObjectsBuilder() {}

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static User buildUser(String email, Role role, boolean isActive, boolean isChoosingPreferences) {
        String namePart = email.split("@")[0];
        return User.builder()
                .name(namePart + "'s Name")
                .surname(namePart + "'s Surname")
                .createdAt(LocalDate.now())
                .address("Ulica 32/2;31-230;Krakow;Polska")
                .email(email)
                .password(passwordEncoder.encode(namePart))
                .role(role)
                .contact("+48123456789")
                .imageBase64("someImageBase64")
                .active(isActive)
                .choosingPreferences(isChoosingPreferences)
                .build();
    }

    public static StudentGuardian buildStudentGuardian(UUID studentId, UUID guardianId) {
        return StudentGuardian.builder()
                .studentId(studentId)
                .guardianId(guardianId)
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

    public static TeacherGroup buildTeacherGroup(UUID teacherId, UUID groupId, UUID subjectId) {
        return TeacherGroup.builder()
                .teacherId(teacherId)
                .groupId(groupId)
                .subjectId(subjectId)
                .build();
    }

    public static Room buildRoom(int capacity, String roomCode) {
        return Room.builder()
                .capacity(capacity)
                .roomCode(roomCode)
                .build();
    }

    public static Attendance buildAttendance(UUID studentId, UUID subjectId, UUID lessonId, AttendanceStatus status) {
        return Attendance.builder()
                .studentId(studentId)
                .subjectId(subjectId)
                .lessonId(lessonId)
                .status(status)
                .build();
    }

    public static PlannedLesson buildPlannedLesson(UUID roomId, UUID groupId, UUID teacherId, UUID subjectId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean active) {
        return PlannedLesson.builder()
                .active(active)
                .roomId(roomId)
                .groupId(groupId)
                .teacherId(teacherId)
                .subjectId(subjectId)
                .weekDay(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public static AssignedLesson buildAssignedLesson(UUID plannedLessonId, LocalDate date, boolean cancelled, boolean modified) {
        return AssignedLesson.builder()
                .plannedLessonId(plannedLessonId)
                .date(date)
                .cancelled(cancelled)
                .modified(modified)
                .build();
    }

    public static TeacherUnavailability buildTeacherUnavailability(UUID teacherId, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek) {
        return TeacherUnavailability.builder()
                .teacherId(teacherId)
                .startTime(startTime)
                .endTime(endTime)
                .weekDay(dayOfWeek)
                .build();
    }

    public static Property buildProperty(String name, PropertyType type, String defaultValue, String value) {
        return Property.builder()
                .name(name)
                .type(type)
                .defaultValue(defaultValue)
                .value(value)
                .build();
    }
}