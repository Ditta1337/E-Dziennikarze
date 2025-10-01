package com.edziennikarze.gradebook.attendance;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/attendance")
@AllArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public Mono<Attendance> createAttendance(@RequestBody Mono<Attendance> attendanceMono) {
        return attendanceService.createAttendance(attendanceMono);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    public Flux<Attendance> getStudentsAttendanceBySubject(@PathVariable UUID studentId, @PathVariable UUID subjectId) {
        return attendanceService.getStudentsAttendanceBySubject(studentId, subjectId);
    }

    @GetMapping("/average/student/{studentId}")
    public Mono<Double> getStudentsAverageAttendance(@PathVariable UUID studentId) {
        return attendanceService.getStudentsAverageAttendance(studentId);
    }

    @GetMapping("/average/student/{studentId}/subject/{subjectId}")
    public Mono<Double> getStudentsAverageAttendanceBySubject(@PathVariable UUID studentId, @PathVariable UUID subjectId) {
        return attendanceService.getStudentsAverageAttendanceBySubject(studentId, subjectId);
    }

    @PutMapping
    public Mono<Attendance> updateAttendance(@RequestBody Mono<Attendance> attendanceMono) {
        return attendanceService.updateAttendance(attendanceMono);
    }
}