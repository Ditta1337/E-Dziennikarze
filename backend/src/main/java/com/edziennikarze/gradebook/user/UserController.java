package com.edziennikarze.gradebook.user;

import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<UserResponse> createUser(@RequestBody Mono<User> userMono) {
        return userService.createUser(userMono);
    }

    @GetMapping("/all")
    public Flux<UserResponse> getAllUsers(@RequestParam(value = "role", required = false) Role role) {
        return userService.getAllUsers(role);
    }

    @GetMapping("/{userId}")
    public Mono<UserResponse> getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping
    public Mono<UserResponse> updateUser(@RequestBody Mono<User> userMono) {
        return userService.updateUser(userMono);
    }

    @PatchMapping("/{userId}/deactivate")
    public Mono<UserResponse> deactivateUser(@PathVariable UUID userId) {
        return userService.deactivateUser(userId);
    }

    @PatchMapping("/{userId}/activate")
    public Mono<UserResponse> activateUser(@PathVariable UUID userId) {
        return userService.activateUser(userId);
    }
}