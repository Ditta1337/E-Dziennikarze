package com.edziennikarze.gradebook.user;

import lombok.AllArgsConstructor;
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
    public Mono<User> createUser(@RequestBody Mono<User> userMono) {
        return userService.createUser(userMono);
    }

    @GetMapping("/all")
    public Flux<User> getAllUsers(@RequestParam(value = "role", required = false) Role role) {
        return userService.getAllUsers(role);
    }

    @GetMapping("/{userId}")
    public Mono<User> getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping
    public Mono<User> updateUser(@RequestBody Mono<User> userMono) {
        return userService.updateUser(userMono);
    }

    @PatchMapping("/{userId}/deactivate")
    public Mono<User> deactivateUser(@PathVariable UUID userId) {
        return userService.deactivateUser(userId);
    }

    @PatchMapping("/{userId}/activate")
    public Mono<User> activateUser(@PathVariable UUID userId) {
        return userService.activateUser(userId);
    }
}
