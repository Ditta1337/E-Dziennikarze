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
    @PostMapping("")
    public Mono<User> createGuardian(@RequestBody Mono<User> userMono){
        return guardianService.createGuardian(userMono);
    }

    @GetMapping("/{uuid}")
    public Mono<User> getGuardian(@PathVariable("uuid") UUID uuid){
        return guardianService.getGuardian(uuid);
    }

    @GetMapping("/all")
    public Flux<User> getAllGuardians(){
        return guardianService.getAllGuardians();
    }

    @DeleteMapping("/{uuid}/delete")
    public Mono<User> deleteGuardian(@PathVariable("uuid") UUID uuid){
        return guardianService.deleteGuardian(uuid);
    }
}
