package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<UserResponse> createUser(@RequestBody Mono<User> userMono) {
        return userService.createUser(userMono);
    }

    @GetMapping("/all")
    public Flux<UserResponse> getAllUsers(@RequestParam(value = "role", required = false) Role role) {
        return userService.getAllUsers(role);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<UserResponse> getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<UserResponse> updateUser(@RequestBody Mono<User> userMono) {
        return userService.updateUser(userMono);
    }

    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<UserResponse> deactivateUser(@PathVariable UUID userId) {
        return userService.deactivateUser(userId);
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<UserResponse> activateUser(@PathVariable UUID userId) {
        return userService.activateUser(userId);
    }
}