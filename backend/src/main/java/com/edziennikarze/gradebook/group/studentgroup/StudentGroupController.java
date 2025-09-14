package com.edziennikarze.gradebook.group.studentgroup;

import static com.edziennikarze.gradebook.user.Role.*;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.user.dto.User;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/student-group")
@AllArgsConstructor
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER})
    public Mono<StudentGroup> createStudentGroup(@RequestBody Mono<StudentGroup> studentGroupMono) {
        return studentGroupService.createStudentGroup(studentGroupMono);
    }

    @GetMapping("/student/{studentId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<Group> getStudentGroups(@PathVariable UUID studentId) {
        return studentGroupService.getAllStudentGroups(studentId);
    }

    @GetMapping("/group/{groupId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<User> getGroupUsers(@PathVariable UUID groupId) {
        return studentGroupService.getAllGroupStudents(groupId);
    }
}
