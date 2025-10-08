package com.edziennikarze.gradebook.attendance;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.user.Role;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final LoggedInUserService loggedInUserService;

    public Mono<Attendance> createAttendance(Mono<Attendance> attendanceMono) {
        return attendanceMono.flatMap(attendanceRepository::save);
    }

    public Flux<Attendance> getStudentsAttendanceBySubject(UUID studentId, UUID subjectId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, Role.TEACHER, Role.PRINCIPAL, Role.OFFICE_WORKER, Role.GUARDIAN)
                .thenMany(attendanceRepository.findAllByStudentIdAndSubjectId(studentId, subjectId));
    }

    public Flux<Attendance> getLessonAttendance(UUID lessonId) {
        return attendanceRepository.findAllByLessonId(lessonId);
    }

    public Mono<Double> getStudentsAverageAttendance(UUID studentId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, Role.TEACHER, Role.PRINCIPAL, Role.OFFICE_WORKER, Role.GUARDIAN)
                .then(attendanceRepository.findAllByStudentId(studentId)
                        .collectList())
                .map(studentAttendance -> {
                    if ( studentAttendance.isEmpty() ) {
                        return 0.0;
                    }
                    long presentCount = studentAttendance.stream()
                            .filter(attendance -> attendance.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    return (double) presentCount / studentAttendance.size();
                });
    }

    public Mono<Double> getStudentsAverageAttendanceBySubject(UUID studentId, UUID subjectId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, Role.TEACHER, Role.PRINCIPAL, Role.OFFICE_WORKER, Role.GUARDIAN)
                .then(attendanceRepository.findAllByStudentIdAndSubjectId(studentId, subjectId)
                        .collectList())
                .map(studentAttendance -> {
                    if ( studentAttendance.isEmpty() ) {
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
                    existingAttendance.setStudentId(attendance.getStudentId());
                    existingAttendance.setSubjectId(attendance.getSubjectId());
                    existingAttendance.setStatus(attendance.getStatus());
                    return attendanceRepository.save(existingAttendance);
                }));
    }
}