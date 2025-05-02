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
    UserService userService;
    @GetMapping("/get/all")
    public Flux<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/get/{uuid}")
    public Mono<User> getUser(@PathVariable("uuid") UUID uuid){
        return userService.getUser(uuid);
    }
    @PutMapping("/update")
    public Mono<User> updateUser(@RequestBody Mono<User> userMono){
        return userService.updateUser(userMono);
    }
    @PatchMapping("/deactivate/{uuid}")
    public Mono<User> deactivateUser(@PathVariable("uuid") UUID uuid){
        return userService.deactivateUser(uuid);
    }

    @PatchMapping("/activate/{uuid}")
    public Mono<User> activateUser(@PathVariable("uuid") UUID uuid){
        return userService.activateUser(uuid);
    }
}
