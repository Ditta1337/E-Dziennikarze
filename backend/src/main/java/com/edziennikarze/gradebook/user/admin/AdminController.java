package com.edziennikarze.gradebook.user.admin;


import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private AdminService adminService;

    @PostMapping("/create")
    public Mono<User> createAdmin(@RequestBody Mono<User> userMono){
        return adminService.createAdmin(userMono);
    }

    @GetMapping("/get/{uuid}")
    public Mono<User> getAdmin(@PathVariable("uuid") UUID uuid){
        return adminService.getAdmin(uuid);
    }

    @GetMapping("/get/all")
    public Flux<User> getAllAdmins(){
        return adminService.getAllAdmins();
    }

    @DeleteMapping("delete/{uuid}")
    public Mono<User> deleteAdmin(@PathVariable("uuid") UUID uuid){
        return adminService.delteAdmin(uuid);
    }
}
