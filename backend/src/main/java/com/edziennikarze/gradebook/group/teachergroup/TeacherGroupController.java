package com.edziennikarze.gradebook.group.teachergroup;

import static com.edziennikarze.gradebook.user.Role.*;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.HasAnyRole;
import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.user.dto.UserResponse;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/teacher-group")
@AllArgsConstructor
public class TeacherGroupController {

    private final TeacherGroupService teacherGroupService;

    @PostMapping
    @HasAnyRole({ ADMIN, OFFICE_WORKER })
    public Mono<TeacherGroup> createTeacherGroup(@RequestBody Mono<TeacherGroup> teacherGroupMono) {
        return teacherGroupService.createTeacherGroup(teacherGroupMono);
    }

    @GetMapping("/teacher/{teacherId}")
    @HasAnyRole({ ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER })
    public Flux<Group> getTeacherGroups(@PathVariable UUID teacherId) {
        return teacherGroupService.getAllTeacherGroups(teacherId);
    }

    @GetMapping("/group/{groupId}")
    @HasAnyRole({ ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER })
    public Flux<UserResponse> getGroupTeachers(@PathVariable UUID groupId) {
        return teacherGroupService.getAllGroupTeachers(groupId);
    }

    @GetMapping("/subject/{subjectId}")
    @HasAnyRole({ ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER })
    public Flux<UserResponse> getSubjectTeachers(@PathVariable UUID subjectId) {
        return teacherGroupService.getAllSubjectTeachers(subjectId);
    }

}
