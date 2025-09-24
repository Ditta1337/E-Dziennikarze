package com.edziennikarze.gradebook.group.teachergroup;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.user.dto.UserResponse;

@RestController
@RequestMapping("/teacher-group")
@AllArgsConstructor
public class TeacherGroupController {

    private final TeacherGroupService teacherGroupService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<TeacherGroup> createTeacherGroup(@RequestBody Mono<TeacherGroup> teacherGroupMono) {
        return teacherGroupService.createTeacherGroup(teacherGroupMono);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Group> getTeacherGroups(@PathVariable UUID teacherId) {
        return teacherGroupService.getAllTeacherGroups(teacherId);
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<UserResponse> getGroupTeachers(@PathVariable UUID groupId) {
        return teacherGroupService.getAllGroupTeachers(groupId);
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<UserResponse> getSubjectTeachers(@PathVariable UUID subjectId) {
        return teacherGroupService.getAllSubjectTeachers(subjectId);
    }
}