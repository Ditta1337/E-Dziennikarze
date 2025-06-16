package com.edziennikarze.gradebook.user.studentguardian;

import com.edziennikarze.gradebook.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/student-guardian")
@RequiredArgsConstructor
public class StudentGuardianController {

    private final StudentGuardianService studentGuardianService;

    @PostMapping
    public Mono<StudentGuardian> createStudentGuardian(@RequestBody Mono<StudentGuardian> studentGuardian) {
        return studentGuardianService.createStudentGuardian(studentGuardian);
    }

    @GetMapping("/guardian/{guardianId}")
    public Flux<User> getGuardiansStudents(@PathVariable UUID guardianId) {
        return studentGuardianService.getAllByGuardianId(guardianId);
    }

    @GetMapping("/student/{studentId}")
    public Flux<User> getStudentsGuardians(@PathVariable UUID studentId) {
        return studentGuardianService.getAllByStudentId(studentId);
    }

    @DeleteMapping("/guardian/{guardianId}/student/{studentId}")
    public Mono<Void> deleteStudentGuardian(@PathVariable UUID guardianId, @PathVariable UUID studentId) {
        return studentGuardianService.deleteByGuardianIdAndStudentID(guardianId, studentId);
    }
}

