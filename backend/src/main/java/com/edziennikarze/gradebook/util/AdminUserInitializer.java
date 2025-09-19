package com.edziennikarze.gradebook.util;

import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "admin@example.com";

    private static final String ADMIN_PASSWORD = "admin";

    @Override
    public void run(String... args) {
        userRepository.findByEmail(ADMIN_EMAIL)
                .hasElement()
                .flatMap(exists -> {
                    if ( Boolean.FALSE.equals(exists) ) {
                        log.info("No admin user found. Creating default admin user with email '{}'", ADMIN_EMAIL);
                        return createDefaultAdmin();
                    } else {
                        log.info("Admin user with email '{}' already exists. Skipping creation.", ADMIN_EMAIL);
                        return Mono.empty();
                    }
                })
                .subscribe();
    }

    private Mono<User> createDefaultAdmin() {
        User adminUser = User.builder()
                .name("Default")
                .surname("Admin")
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .contact("N/A")
                .address("N/A")
                .createdAt(LocalDate.now())
                .active(true)
                .choosingPreferences(false)
                .build();

        return userRepository.save(adminUser)
                .doOnSuccess(user -> log.info("Default admin user created successfully with ID: {}", user.getId()));
    }
}
