package com.edziennikarze.gradebook.user.teacher;

import com.edziennikarze.gradebook.user.User;
import com.edziennikarze.gradebook.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TeacherService {
    UserService userService;
    TeacherRepository teacherRepository;

    public Mono<User> createTeacher(Mono<User> userMono) {
        return userService.createUser(userMono)
                .flatMap(savedUser -> {
                    Teacher teacher = Teacher.builder()
                            .userId(savedUser.getId())
                            .build();

                    return teacherRepository
                            .save(teacher)
                            .thenReturn(savedUser);
                });
    }

    public Mono<User> getTeacher(UUID uuid) {
        Mono<Teacher> teacherMono = teacherRepository.findById(uuid);
        return teacherMono.flatMap(teacher -> userService.getUser(teacher.getUserId()));
    }

    public Flux<User> getAllTeachers() {
        Flux<Teacher> teacherFlux = teacherRepository.findAll();
        return teacherFlux.flatMap(teacher -> userService.getUser(teacher.getUserId()));
    }

    public Mono<Teacher> getTeacherByUserId(UUID userId) {
        return teacherRepository.findByUserId(userId);
    }
}
