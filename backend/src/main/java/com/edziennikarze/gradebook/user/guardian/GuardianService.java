package com.edziennikarze.gradebook.user.guardian;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GuardianService {
    private GuardianRepository guardianRepository;
    private UserService userService;

    public Mono<User> createGuardian(Mono<User> userMono) {
        return userService.createUser(userMono)
                .flatMap(savedUser -> {
                    Guardian guardian = Guardian.builder()
                            .userId(savedUser.getId())
                            .build();

                    return guardianRepository
                            .save(guardian)
                            .thenReturn(savedUser);
                });
    }

    public Mono<User> getGuardian(UUID uuid) {
        Mono<Guardian> guardianMono = guardianRepository.findById(uuid);
        return guardianMono.flatMap(guardian -> userService.getUser(guardian.getUserId()));
    }

    public Flux<User> getAllGuardians() {
        Flux<Guardian> guardianFlux = guardianRepository.findAll();
        return guardianFlux.flatMap(guardian -> userService.getUser(guardian.getUserId()));
    }

    public Mono<User> deleteGuardian(UUID uuid) {
        return guardianRepository.findById(uuid)
                .flatMap(guardian -> guardianRepository.deleteById(uuid)
                        .then(userService.deleteUser(guardian.getUserId())));
    }
}