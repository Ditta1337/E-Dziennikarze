package com.edziennikarze.gradebook.attendance;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public Mono<Attendance> createAttendance(Mono<Attendance> attendanceMono) {
        return attendanceMono.flatMap(attendanceRepository::save);
    }

    public Flux<Attendance> getStudentsAttendanceBySubject(UUID studentId, UUID subjectId) {
        return attendanceRepository.findAllByStudentIdAndSubjectId(studentId, subjectId);
    }

    public double getStudentsAverageAttendance(UUID studentId) {
        List<Attendance> studentAttendance = attendanceRepository.findAllByStudentId(studentId)
                .collectList()
                .block();

        if ( studentAttendance.isEmpty() ) {
            return 0.0;
        }

        List<Attendance> studentAttendancePresent = studentAttendance.stream()
                .filter(Attendance::isPresent)
                .toList();

        return (double) studentAttendancePresent.size() / studentAttendance.size();
    }

    public double getStudentsAverageAttendanceBySubject(UUID studentId, UUID subjectId) {
        List<Attendance> studentAttendance = attendanceRepository.findAllByStudentIdAndSubjectId(studentId, subjectId)
                .collectList()
                .block();

        if ( studentAttendance.isEmpty() ) {
            return 0.0;
        }

        List<Attendance> studentAttendancePresent = studentAttendance.stream()
                .filter(Attendance::isPresent)
                .toList();

        return (double) studentAttendancePresent.size() / studentAttendance.size();
    }

    public Mono<Attendance> updateAttendance(Mono<Attendance> attendanceMono) {
        return attendanceMono.flatMap(attendance -> attendanceRepository.findById(attendance.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Attendance with id " + attendance.getId() + " not found")))
                .flatMap(existingAttendance -> {
                    existingAttendance.setStudentId(attendance.getStudentId());
                    existingAttendance.setSubjectId(attendance.getSubjectId());
                    existingAttendance.setPresent(attendance.isPresent());
                    return attendanceRepository.save(existingAttendance);
                }));
    }
}
