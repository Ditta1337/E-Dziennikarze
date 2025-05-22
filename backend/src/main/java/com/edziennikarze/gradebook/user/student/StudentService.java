package com.edziennikarze.gradebook.user.student;

import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
import com.edziennikarze.gradebook.user.guardian.Guardian;
import com.edziennikarze.gradebook.user.guardian.GuardianService;
import com.edziennikarze.gradebook.user.student.dto.StudentsGuardianDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class StudentService {
    private final UserService userService;
    private final StudentRepository studentRepository;
    private final GuardianService guardianService;

    public Mono<User> createStudent(Mono<User> userMono) {
        return userService.createUser(userMono)
                .flatMap(savedUser -> {
                    Student student = Student.builder()
                            .userId(savedUser.getId())
                            .canChoosePreferences(false)
                            .guardianId(null)
                            .build();

                    return studentRepository
                            .save(student)
                            .thenReturn(savedUser);
                });
    }

    public Mono<User> getStudent(UUID uuid) {
        Mono<Student> studentMono = studentRepository.findById(uuid);
        return studentMono.flatMap(student -> userService.getUser(student.getUserId()));
    }

    public Flux<User> getAllStudents() {
        Flux<Student> studentFlux = studentRepository.findAll();
        return studentFlux.flatMap(student -> userService.getUser(student.getUserId()));
    }

    public Mono<Student> setStudentsGuardian(Mono<StudentsGuardianDTO> studentsGuardianMono) {
        return studentsGuardianMono.flatMap(studentsGuardianDTO ->
                Mono.zip(
                        userService.getUser(studentsGuardianDTO.getStudentId()),
                        userService.getUser(studentsGuardianDTO.getGuardianId())
                ).flatMap(tuple -> {
                    User studentUser = tuple.getT1();
                    User guardianUser = tuple.getT2();

                    if (studentUser.getRole() != Role.STUDENT) {
                        return Mono.error(new IllegalArgumentException("User is not a student"));
                    }

                    if (guardianUser.getRole() != Role.GUARDIAN) {
                        return Mono.error(new IllegalArgumentException("User is not a guardian"));
                    }

                    return Mono.zip(
                            guardianService.getGuardianByUserId(guardianUser.getId()),
                            getStudentByUserId(studentUser.getId())
                    ).flatMap(data -> {
                        Guardian guardian = data.getT1();
                        Student student = data.getT2();

                        student.setGuardianId(guardian.getId());
                        return studentRepository.save(student);
                    });
                })
        );
    }

    public Mono<Student> setStudentPreferencesCapabilities(UUID uuid, boolean capabilities) {
        return studentRepository.findById(uuid)
                .flatMap(student -> {
                    student.setCanChoosePreferences(capabilities);
                    return studentRepository.save(student);
                });
    }

    public Mono<Student> getStudentByUserId(UUID userId) {
        return studentRepository.findByUserId(userId);
    }

    public Mono<User> getStudentGuardian(UUID studentUserId) {
        return getStudentByUserId(studentUserId).flatMap(student -> guardianService.getGuardian(student.getGuardianId()));
    }
}
