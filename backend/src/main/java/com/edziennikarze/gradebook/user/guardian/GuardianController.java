package com.edziennikarze.gradebook.user.guardian;

import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/guardian")
@AllArgsConstructor
public class GuardianController {
    private GuardianService guardianService;

    @PostMapping()
    public Mono<User> createGuardian(@RequestBody Mono<User> userMono) {
        return guardianService.createGuardian(userMono);
    }

    @GetMapping("/all")
    public Flux<User> getAllGuardians() {
        return guardianService.getAllGuardians();
    }
}
