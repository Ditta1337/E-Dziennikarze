package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.studentguardian.StudentGuardianRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final StudentGuardianRepository studentGuardianRepository;

    public Mono<User> createUser(Mono<User> userMono) {
        return userMono.flatMap(user -> userRepository.save(user));
    }

    public Flux<User> getAllUsers(Role role) {
        return role != null ? userRepository.findAllByRole(role) : userRepository.findAll();
    }

    public Mono<User> getUser(UUID userId) {
        return userRepository.findById(userId);
    }

    public Mono<User> updateUser(Mono<User> userMono) {
        return userMono.flatMap(user ->
                userRepository.findById(user.getId())
                        .flatMap(existingUser -> {
                            existingUser.setName(user.getName());
                            existingUser.setSurname(user.getSurname());
                            existingUser.setAddress(user.getAddress());
                            existingUser.setEmail(user.getEmail());
                            existingUser.setPassword(user.getPassword());
                            existingUser.setContact(user.getContact());
                            existingUser.setImageBase64(user.getImageBase64());
                            existingUser.setRole(user.getRole());
                            existingUser.setActive(user.isActive());
                            existingUser.setChoosingPreferences(user.isChoosingPreferences());
                            return userRepository.save(existingUser);
                        }));
    }

    public Mono<User> deactivateUser(UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User with id " + userId + " not found")))
                .flatMap(foundUser -> {
                    Mono<Void> cleanupMono;

                    switch (foundUser.getRole()) {
                        case TEACHER:
                            cleanupMono = subjectTaughtRepository.deleteAllByTeacherId(foundUser.getId());
                            break;
                        case STUDENT:
                            cleanupMono = studentGuardianRepository.deleteAllByStudentId(foundUser.getId());
                            break;
                        case GUARDIAN:
                            cleanupMono = studentGuardianRepository.deleteAllByGuardianId(foundUser.getId());
                            break;
                        default:
                            return Mono.error(new ResourceNotFoundException("User with unexpected role " + foundUser.getRole() + " found"));
                    }

                    foundUser.setActive(false);
                    return cleanupMono.then(userRepository.save(foundUser));
                });
    }


    public Mono<User> activateUser(UUID userId) {
        return userRepository.findById(userId)
                .flatMap(exisitingUser -> {
                    exisitingUser.setActive(true);
                    return userRepository.save(exisitingUser);
                });
    }
}
