package com.edziennikarze.gradebook.user.officeworker;

import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/worker")
@AllArgsConstructor
public class OfficeWorkerController {
    private final OfficeWorkerService officeWorkerService;

    @PostMapping("/{principalPrivileges}")
    public Mono<User> createOfficeWorker(@RequestBody Mono<User> userMono, @PathVariable("principalPrivileges") boolean principalPrivileges){
        return officeWorkerService.createOfficeWorker(userMono, principalPrivileges);
    }

    @GetMapping("/{uuid}")
    public Mono<User> getOfficeWorker(@PathVariable("uuid") UUID uuid){
        return officeWorkerService.getOfficeWorker(uuid);
    }

    @GetMapping("/all")
    public Flux<User> getAllOfficeWorkers(){
        return officeWorkerService.getAllOfficeWorkers();
    }

    @DeleteMapping("/{uuid}/delete")
    public Mono<User> deleteOfficeWorker(@PathVariable("uuid") UUID uuid){
        return officeWorkerService.deleteOfficeWorker(uuid);
    }
}
