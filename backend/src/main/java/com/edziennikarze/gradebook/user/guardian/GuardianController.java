package com.edziennikarze.gradebook.user.guardian;

import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/guardian")
@AllArgsConstructor
public class GuardianController {
    private GuardianService guardianService;
    @PostMapping("/create")
    public Mono<User> createGuardian(@RequestBody Mono<User> userMono){
        return guardianService.createGuardian(userMono);
    }

    @GetMapping("/get/{uuid}")
    public Mono<User> getGuardian(@PathVariable("uuid") UUID uuid){
        return guardianService.getGuardian(uuid);
    }

    @GetMapping("/get/all")
    public Flux<User> getAllGuardians(){
        return guardianService.getAllGuardians();
    }

    @DeleteMapping("/delete/{uuid}")
    public Mono<User> deleteGuardian(@PathVariable("uuid") UUID uuid){
        return guardianService.deleteGuardian(uuid);
    }
}
