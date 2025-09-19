package com.edziennikarze.gradebook.attendance;

import static com.edziennikarze.gradebook.user.Role.*;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/attendance")
@AllArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Mono<Attendance> createAttendance(@RequestBody Mono<Attendance> attendanceMono) {
        return attendanceService.createAttendance(attendanceMono);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<Attendance> getStudentsAttendanceBySubject(@PathVariable UUID studentId, @PathVariable UUID subjectId) {
        return attendanceService.getStudentsAttendanceBySubject(studentId, subjectId);
    }

    @GetMapping("/average/student/{studentId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Mono<Double> getStudentsAverageAttendance(@PathVariable UUID studentId) {
        return attendanceService.getStudentsAverageAttendance(studentId);
    }

    @GetMapping("/average/student/{studentId}/subject/{subjectId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Mono<Double> getStudentsAverageAttendanceBySubject(@PathVariable UUID studentId, @PathVariable UUID subjectId) {
        return attendanceService.getStudentsAverageAttendanceBySubject(studentId, subjectId);
    }

    @PutMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER, TEACHER})
    public Mono<Attendance> updateAttendance(@RequestBody Mono<Attendance> attendanceMono) {
        return attendanceService.updateAttendance(attendanceMono);
    }
}
