package com.edziennikarze.gradebook.user.admin;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminService {
    AdminRepository adminRepository;
    UserService userService;

    public Mono<User> createAdmin(Mono<User> userMono) {
        return userService.createUser(userMono)
                .flatMap(savedUser -> {
                    Admin admin = new Admin();
                    admin.setUserId(savedUser.getId());

                    return adminRepository
                            .save(admin)
                            .thenReturn(savedUser);
                });
    }

    public Mono<User> getAdmin(UUID uuid) {
        Mono<Admin> adminMono = adminRepository.findById(uuid);
        return adminMono.flatMap(admin -> userService.getUser(admin.getUserId()));
    }

    public Flux<User> getAllAdmins() {
        Flux<Admin> adminFlux = adminRepository.findAll();
        return adminFlux.flatMap(admin -> userService.getUser(admin.getUserId()));
    }

    public Mono<User> delteAdmin(UUID uuid) {
        return adminRepository.findById(uuid)
                .flatMap(admin -> adminRepository.deleteById(uuid)
                        .then(userService.deleteUser(admin.getUserId())));
    }
}