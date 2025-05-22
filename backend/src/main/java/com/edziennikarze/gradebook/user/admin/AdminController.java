package com.edziennikarze.gradebook.user.admin;


import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private AdminService adminService;

    @PostMapping()
    public Mono<User> createAdmin(@RequestBody Mono<User> userMono) {
        return adminService.createAdmin(userMono);
    }

    @GetMapping("/all")
    public Flux<User> getAllAdmins() {
        return adminService.getAllAdmins();
    }
}
