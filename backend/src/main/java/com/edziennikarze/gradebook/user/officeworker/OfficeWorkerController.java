package com.edziennikarze.gradebook.user.officeworker;

import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/office/worker")
@AllArgsConstructor
public class OfficeWorkerController {
    private OfficeWorkerService officeWorkerService;

    @PostMapping("/create/{principalPrivileges}")
    public Mono<User> createOfficeWorker(@RequestBody Mono<User> userMono, @PathVariable("principalPrivileges") boolean principalPrivileges){
        return officeWorkerService.createOfficeWorker(userMono, principalPrivileges);
    }

    @GetMapping("/get/{uuid}")
    public Mono<User> getOfficeWorker(@PathVariable("uuid") UUID uuid){
        return officeWorkerService.getOfficeWorker(uuid);
    }

    @GetMapping("get/all")
    public Flux<User> getAllOfficeWorkers(){
        return officeWorkerService.getAllOfficeWorkers();
    }

    @DeleteMapping("delete/{uuid}")
    public Mono<User> deleteOfficeWorker(@PathVariable("uuid") UUID uuid){
        return officeWorkerService.deleteOfficeWorker(uuid);
    }
}
