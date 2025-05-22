package com.edziennikarze.gradebook.user.teacher;

import com.edziennikarze.gradebook.user.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/teacher")
@AllArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @PostMapping()
    public Mono<User> createTeacher(@RequestBody Mono<User> userMono) {
        return teacherService.createTeacher(userMono);
    }

    @GetMapping("/all")
    public Flux<User> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

}
