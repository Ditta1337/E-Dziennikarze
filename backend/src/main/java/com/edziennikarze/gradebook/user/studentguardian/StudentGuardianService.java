package com.edziennikarze.gradebook.user.studentguardian;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentGuardianService {

    private final StudentGuardianRepository studentGuardianRepository;

    private final UserRepository userRepository;

    public Mono<StudentGuardian> createStudentGuardian(Mono<StudentGuardian> studentGuardian) {
        return studentGuardian.flatMap(studentGuardianRepository::save);
    }

    public Flux<User> getAllByGuardianId(UUID guardianId) {
        return studentGuardianRepository.findAllByGuardianId(guardianId)
                .flatMap(studentGuardian -> userRepository.findById(studentGuardian.getStudentId()));
    }

    public Flux<User> getAllByStudentId(UUID studentId) {
        return studentGuardianRepository.findAllByStudentId(studentId)
                .flatMap(studentGuardian -> userRepository.findById(studentGuardian.getStudentId()));
    }

    public Mono<Void> deleteByGuardianIdAndStudentId(UUID guardianId, UUID studentId) {
        return studentGuardianRepository.deleteByGuardianIdAndStudentId(guardianId, studentId);
    }
}
