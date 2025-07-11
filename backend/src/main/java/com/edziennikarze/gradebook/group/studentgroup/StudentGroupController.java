package com.edziennikarze.gradebook.group.studentgroup;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.user.User;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/student-group")
@AllArgsConstructor
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
    public Flux<User> getGroupUsers(@PathVariable UUID groupId) {
        return studentGroupService.getAllGroupStudents(groupId);
    }
}
