package com.edziennikarze.gradebook.attendance;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.notification.NotificationService;
import com.edziennikarze.gradebook.subject.SubjectRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

import static com.edziennikarze.gradebook.user.Role.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final SubjectRepository subjectRepository;

    private final LoggedInUserService loggedInUserService;

    private final NotificationService notificationService;

    public Mono<Attendance> createAttendance(Mono<Attendance> attendanceMono) {
        return attendanceMono.flatMap(attendanceRepository::save)
                .flatMap(attendance -> sendNotification(attendance, "Dodano nową obecność: " + attendance.getStatus().getDisplayName()));
    }

    public Flux<Attendance> getStudentsAttendanceBySubject(UUID studentId, UUID subjectId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .thenMany(attendanceRepository.findAllByStudentIdAndSubjectId(studentId, subjectId));
    }

    public Flux<Attendance> getLessonAttendance(UUID lessonId) {
        return attendanceRepository.findAllByLessonId(lessonId);
    }

    public Mono<Double> getStudentsAverageAttendance(UUID studentId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .then(attendanceRepository.findAllByStudentId(studentId)
                        .collectList())
                .map(studentAttendance -> {
                    if (studentAttendance.isEmpty()) {
                        return 0.0;
                    }
                    long presentCount = studentAttendance.stream()
                            .filter(attendance -> attendance.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    return (double) presentCount / studentAttendance.size();
                });
    }

    public Mono<Double> getStudentsAverageAttendanceBySubject(UUID studentId, UUID subjectId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, TEACHER, PRINCIPAL, OFFICE_WORKER, GUARDIAN)
                .then(attendanceRepository.findAllByStudentIdAndSubjectId(studentId, subjectId)
                        .collectList())
                .map(studentAttendance -> {
                    if (studentAttendance.isEmpty()) {
                        return 0.0;
                    }
                    long presentCount = studentAttendance.stream()
                            .filter(attendance -> attendance.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    return (double) presentCount / studentAttendance.size();
                });
    }

    public Mono<Attendance> updateAttendance(Mono<Attendance> attendanceMono) {
        return attendanceMono.flatMap(attendance -> attendanceRepository.findById(attendance.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Attendance with id " + attendance.getId() + " not found")))
                .flatMap(existingAttendance -> {
                    AttendanceStatus oldStatus = existingAttendance.getStatus();
                    existingAttendance.setStudentId(attendance.getStudentId());
                    existingAttendance.setSubjectId(attendance.getSubjectId());
                    existingAttendance.setStatus(attendance.getStatus());

                    return sendNotification(attendance, String.format("Zmodyfikowano obecność z %s na %s", oldStatus.getDisplayName(), attendance.getStatus().getDisplayName()))
                            .then(attendanceRepository.save(existingAttendance));
                }));
    }

    private Mono<Attendance> sendNotification(Attendance attendance, String message) {
        return subjectRepository.findById(attendance.getSubjectId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Subject with id " + attendance.getId() + " not found")))
                .flatMap(subject -> {
                    UUID recipientId = attendance.getStudentId();
                    String detailedMessage = String.format(
                            "%s dla przedmiotu: %s",
                            message,
                            subject.getName()
                    );
                    return notificationService.sendNotification(recipientId, detailedMessage)
                            .thenReturn(attendance);
                });
    }
}