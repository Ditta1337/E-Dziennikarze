package com.edziennikarze.gradebook.user.teacher;

import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/teacher")
@AllArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @PostMapping()
    public Mono<User> create(@RequestBody Mono<User> userMono) {
        return teacherService.createTeacher(userMono);
    }

    @GetMapping("/{uuid}")
    public Mono<User> get(@PathVariable("uuid") UUID uuid) {
        return teacherService.getTeacher(uuid);
    }

    @GetMapping("/all")
    public Flux<User> getAll() {
        return teacherService.getAllTeachers();
    }

    @DeleteMapping("/{uuid}/delete")
    public Mono<User> delete(@PathVariable("uuid") UUID uuid) {
        return teacherService.deleteTeacher(uuid);
    }
}
