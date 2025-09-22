package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.attendance.AttendanceRepository;
import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.exception.UserAlreadyExistsException;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.group.teachergroup.TeacherGroupRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.dto.UserResponse;
import com.edziennikarze.gradebook.user.studentguardian.StudentGuardianRepository;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final StudentGuardianRepository studentGuardianRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final AttendanceRepository attendanceRepository;

    private final TeacherGroupRepository teacherGroupRepository;

    private final PasswordEncoder passwordEncoder;

    private final LoggedInUserService loggedInUserService;

    public Mono<UserResponse> createUser(Mono<User> userMono) {
        return userMono.flatMap(this::validateUserDoesNotExist)
                .map(this::prepareNewUser)
                .flatMap(userRepository::save)
                .map(UserResponse::from);
    }

    public Flux<UserResponse> getAllUsers(Role role) {
        Flux<User> users = role != null ? userRepository.findAllByRole(role) : userRepository.findAll();
        return users.map(UserResponse::from);
    }

    public Mono<UserResponse> getUser(UUID userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from);
    }

    public Mono<UserResponse> updateUser(Mono<User> userMono) {
        return userMono.flatMap(user -> loggedInUserService.isSelfOrAllowedRoleElseThrow(user.getId())
                .then(userRepository.findById(user.getId()))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User with id " + user.getId() + " not found")))
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setSurname(user.getSurname());
                    existingUser.setAddress(user.getAddress());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setContact(user.getContact());
                    existingUser.setImageBase64(user.getImageBase64());
                    existingUser.setRole(user.getRole());
                    existingUser.setActive(user.isActive());
                    existingUser.setChoosingPreferences(user.isChoosingPreferences());

                    if ( user.getPassword() != null && !user.getPassword()
                            .isEmpty() ) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }

                    return userRepository.save(existingUser);
                })
                .map(UserResponse::from));
    }

    public Mono<UserResponse> deactivateUser(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User with id " + userId + " not found")))
                .flatMap(foundUser -> {
                    UUID foundUserId = foundUser.getId();
                    Mono<Void> cleanupMono = switch (foundUser.getRole()) {
                        case TEACHER -> deleteTeacherFromRelatedTables(foundUserId);
                        case STUDENT -> deleteStudentFromRelatedTables(foundUserId);
                        case GUARDIAN -> studentGuardianRepository.deleteAllByGuardianId(foundUserId);
                        default -> Mono.empty();
                    };

                    foundUser.setActive(false);
                    return cleanupMono.then(userRepository.save(foundUser));
                })
                .map(UserResponse::from);
    }

    public Mono<UserResponse> activateUser(UUID userId) {
        return userRepository.findById(userId)
                .flatMap(exisitingUser -> {
                    exisitingUser.setActive(true);
                    return userRepository.save(exisitingUser);
                })
                .map(UserResponse::from);
    }

    private Mono<Void> deleteStudentFromRelatedTables(UUID studentId) {
        return studentGuardianRepository.deleteAllByStudentId(studentId)
                .then(studentGroupRepository.deleteAllByStudentId(studentId))
                .then(attendanceRepository.deleteByStudentId(studentId));
    }

    private Mono<Void> deleteTeacherFromRelatedTables(UUID teacherId) {
        return subjectTaughtRepository.deleteAllByTeacherId(teacherId)
                .then(teacherGroupRepository.deleteAllByTeacherId(teacherId));
    }

    private Mono<User> validateUserDoesNotExist(User user) {
        return userRepository.findByEmail(user.getEmail())
                .hasElement()
                .flatMap(emailExists -> {
                    if ( Boolean.TRUE.equals(emailExists) ) {
                        return Mono.error(new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists."));
                    }
                    return Mono.just(user);
                });
    }

    private User prepareNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDate.now());
        return user;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmail(email)
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User with email " + email + " not found")));
    }
}

