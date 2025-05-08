package com.edziennikarze.gradebook.user.student;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
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

    public Mono<User> deleteStudent(UUID uuid) {
        return studentRepository.findById(uuid)
                .flatMap(student -> studentRepository.deleteById(uuid)
                        .then(userService.deleteUser(student.getUserId())));
    }

    public Mono<Student> setStudentsGuardian(Mono<StudentsGuardianDTO> studentsGuardianMono) {
        return studentsGuardianMono.flatMap(studentsGuardianDTO -> studentRepository.findById(studentsGuardianDTO.getStudentId())
                .flatMap(student -> {
                    student.setGuardianId(studentsGuardianDTO.getGuardianId());
                    return studentRepository.save(student);
                }));
    }

    public Mono<Student> setStudentPreferencesCapabilities(UUID uuid, boolean capabilities) {
        return studentRepository.findById(uuid)
                .flatMap(student -> {
                    student.setCanChoosePreferences(capabilities);
                    return studentRepository.save(student);
                });
    }
}
