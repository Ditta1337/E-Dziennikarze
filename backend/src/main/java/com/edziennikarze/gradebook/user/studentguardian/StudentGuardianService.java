package com.edziennikarze.gradebook.user.studentguardian;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;

import com.edziennikarze.gradebook.user.dto.UserResponse;
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

    private final LoggedInUserService loggedInUserService;

    public Mono<StudentGuardian> createStudentGuardian(Mono<StudentGuardian> studentGuardian) {
        return studentGuardian.flatMap(studentGuardianRepository::save);
    }

    public Flux<UserResponse> getAllStudentGuardians() {
        return studentGuardianRepository.findAll()
                .flatMap(studentGuardian -> userRepository.findById(studentGuardian.getGuardianId()))
                .map(UserResponse::from);
    }

    public Flux<UserResponse> getAllByGuardianId(UUID guardianId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(guardianId, Role.OFFICE_WORKER, Role.PRINCIPAL, Role.TEACHER)
                .thenMany(studentGuardianRepository.findAllByGuardianId(guardianId)
                        .flatMap(studentGuardian -> userRepository.findById(studentGuardian.getStudentId()))
                        .map(UserResponse::from));
    }

    public Flux<UserResponse> getAllByStudentId(UUID studentId) {
        return loggedInUserService.isSelfOrAllowedRoleElseThrow(studentId, Role.OFFICE_WORKER, Role.PRINCIPAL, Role.TEACHER)
                .thenMany(studentGuardianRepository.findAllByStudentId(studentId)
                        .flatMap(studentGuardian -> userRepository.findById(studentGuardian.getGuardianId()))
                        .map(UserResponse::from));
    }

    public Mono<Void> deleteByGuardianIdAndStudentId(UUID guardianId, UUID studentId) {
        return studentGuardianRepository.deleteByGuardianIdAndStudentId(guardianId, studentId);
    }
}
