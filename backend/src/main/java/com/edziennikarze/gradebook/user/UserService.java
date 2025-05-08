package com.edziennikarze.gradebook.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public Mono<User> createUser(Mono<User> userMono) {
        return userMono.flatMap(user -> userRepository.save(user));
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
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
                            existingUser.setIsActive(user.getIsActive());
                            return userRepository.save(existingUser);
                        }));
    }

    public Mono<User> deactivateUser(UUID uuid) {
        return userRepository.findById(uuid)
                .flatMap(exisitingUser -> {
                    exisitingUser.setIsActive(false);
                    return userRepository.save(exisitingUser);
                });
    }

    public Mono<User> activateUser(UUID uuid) {
        return userRepository.findById(uuid)
                .flatMap(exisitingUser -> {
                    exisitingUser.setIsActive(true);
                    return userRepository.save(exisitingUser);
                });
    }

    public Mono<User> deleteUser(UUID uuid) {
        return userRepository.findById(uuid)
                .flatMap(user -> userRepository.deleteById(uuid)
                        .thenReturn(user));
    }
}
