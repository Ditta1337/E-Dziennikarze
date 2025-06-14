package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final SubjectTaughtService subjectTaughtService;

    public Mono<User> createUser(Mono<User> userMono) {
        return userMono.flatMap(user -> userRepository.save(user));
    }

    public Flux<User> getAllUsers(Role role) {
        return role != null ? userRepository.findAllByRole(role) : userRepository.findAll();
    }

    public Flux<User> getAllUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    public Mono<User> getUser(UUID uuid) {
        return userRepository.findById(uuid);
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

    public Mono<User> deactivateUser(UUID uuid) {
        User foundUser = userRepository.findById(uuid).block();

        if (foundUser == null) {
            return Mono.error(new ResourceNotFoundException("User with id " + uuid + " not found"));
        }

        if (foundUser.getRole() == Role.TEACHER) {
            subjectTaughtService.deleteSubjectsTaughtByTeacher(foundUser.getId());
        }

        foundUser.setActive(false);
        return userRepository.save(foundUser);
    }

    public Mono<User> activateUser(UUID uuid) {
        return userRepository.findById(uuid)
                .flatMap(exisitingUser -> {
                    exisitingUser.setActive(true);
                    return userRepository.save(exisitingUser);
                });
    }
}
