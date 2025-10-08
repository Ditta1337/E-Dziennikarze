package com.edziennikarze.gradebook.group.studentgroup;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.user.dto.UserResponse;

@RestController
@RequestMapping("/student-group")
@RequiredArgsConstructor
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    @PostMapping
    public Mono<StudentGroup> createStudentGroup(@RequestBody Mono<StudentGroup> studentGroupMono) {
        return studentGroupService.createStudentGroup(studentGroupMono);
    }

    @GetMapping("/student/{studentId}")
    public Flux<Group> getStudentGroups(@PathVariable UUID studentId) {
        return studentGroupService.getAllStudentGroups(studentId);
    }

    @GetMapping("/group/{groupId}")
    public Flux<UserResponse> getGroupUsers(@PathVariable UUID groupId) {
        return studentGroupService.getAllGroupStudents(groupId);
    }
}