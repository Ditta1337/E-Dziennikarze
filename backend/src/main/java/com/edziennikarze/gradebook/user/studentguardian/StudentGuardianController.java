package com.edziennikarze.gradebook.user.studentguardian;

import static com.edziennikarze.gradebook.user.Role.*;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;
import com.edziennikarze.gradebook.user.dto.User;
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
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<StudentGuardian> createStudentGuardian(@RequestBody Mono<StudentGuardian> studentGuardian) {
        return studentGuardianService.createStudentGuardian(studentGuardian);
    }

    @GetMapping("/guardian/{guardianId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<User> getGuardiansStudents(@PathVariable UUID guardianId) {
        return studentGuardianService.getAllByGuardianId(guardianId);
    }

    @GetMapping("/student/{studentId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<User> getStudentsGuardians(@PathVariable UUID studentId) {
        return studentGuardianService.getAllByStudentId(studentId);
    }

    @DeleteMapping("/guardian/{guardianId}/student/{studentId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<Void> deleteStudentGuardian(@PathVariable UUID guardianId, @PathVariable UUID studentId) {
        return studentGuardianService.deleteByGuardianIdAndStudentId(guardianId, studentId);
    }
}

