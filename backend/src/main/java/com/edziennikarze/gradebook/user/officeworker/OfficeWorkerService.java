package com.edziennikarze.gradebook.user.officeworker;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class OfficeWorkerService {
    OfficeWorkerRepository officeWorkerRepository;
    UserService userService;

    public Mono<User> createOfficeWorker(Mono<User> userMono, boolean principalPrivileges){
        return userService.createUser(userMono)
                .flatMap(savedUser -> {
                    OfficeWorker officeWorker = OfficeWorker.builder()
                            .userId(savedUser.getId())
                            .principalPriviledge(principalPrivileges)
                            .build();

                    return officeWorkerRepository
                            .save(officeWorker)
                            .thenReturn(savedUser);
                });
    }

    public Mono<User> getOfficeWorker(UUID uuid){
        Mono<OfficeWorker> officeWorkerMono = officeWorkerRepository.findById(uuid);
        return officeWorkerMono.flatMap(officeWorker -> userService.getUser(officeWorker.getUserId()));
    }

    public Flux<User> getAllOfficeWorkers() {
        Flux<OfficeWorker> officeWorkerFlux = officeWorkerRepository.findAll();
        return officeWorkerFlux.flatMap(officeWorker -> userService.getUser(officeWorker.getUserId()));
    }

    public Mono<User> deleteOfficeWorker(UUID uuid){
        return officeWorkerRepository.findById(uuid)
                .flatMap(officeWorker -> officeWorkerRepository.deleteById(uuid)
                .then(userService.deleteUser(officeWorker.getUserId())));
    }
}
