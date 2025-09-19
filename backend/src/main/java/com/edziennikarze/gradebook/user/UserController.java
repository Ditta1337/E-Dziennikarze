package com.edziennikarze.gradebook.user;

import static com.edziennikarze.gradebook.user.Role.*;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.dto.UserResponse;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<UserResponse> createUser(@RequestBody Mono<User> userMono) {
        return userService.createUser(userMono);
    }

    @GetMapping("/all")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Flux<UserResponse> getAllUsers(@RequestParam(value = "role", required = false) Role role) {
        return userService.getAllUsers(role);
    }

    @GetMapping("/{userId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<UserResponse> getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PutMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<UserResponse> updateUser(@RequestBody Mono<User> userMono) {
        return userService.updateUser(userMono);
    }

    @PatchMapping("/{userId}/deactivate")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<UserResponse> deactivateUser(@PathVariable UUID userId) {
        return userService.deactivateUser(userId);
    }

    @PatchMapping("/{userId}/activate")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<UserResponse> activateUser(@PathVariable UUID userId) {
        return userService.activateUser(userId);
    }
}
