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
    private UserService userService;

    @GetMapping("/all")
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{uuid}")
    public Mono<User> getUser(@PathVariable("uuid") UUID uuid) {
        return userService.getUser(uuid);
    }

    @PutMapping("/update")
    public Mono<User> updateUser(@RequestBody Mono<User> userMono) {
        return userService.updateUser(userMono);
    }

    @PatchMapping("/{uuid}/deactivate")
    public Mono<User> deactivateUser(@PathVariable("uuid") UUID uuid) {
        return userService.deactivateUser(uuid);
    }

    @PatchMapping("/{uuid}/activate")
    public Mono<User> activateUser(@PathVariable("uuid") UUID uuid) {
        return userService.activateUser(uuid);
    }
}
