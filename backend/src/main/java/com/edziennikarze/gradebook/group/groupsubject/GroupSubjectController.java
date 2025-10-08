package com.edziennikarze.gradebook.group.groupsubject;

import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubjectResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.user.dto.UserResponse;

@RestController
@RequestMapping("/group-subject")
@AllArgsConstructor
public class GroupSubjectController {

    private final GroupSubjectService groupSubjectService;

    @PostMapping
    public Mono<GroupSubject> createTeacherGroup(@RequestBody Mono<GroupSubject> teacherGroupMono) {
        return groupSubjectService.createTeacherGroup(teacherGroupMono);
    }

    @GetMapping("/teacher/{teacherId}")
    public Flux<Group> getTeacherGroups(@PathVariable UUID teacherId) {
        return groupSubjectService.getAllTeacherGroups(teacherId);
    }

    @GetMapping("/group/{groupId}")
    public Flux<UserResponse> getGroupTeachers(@PathVariable UUID groupId) {
        return groupSubjectService.getAllGroupTeachers(groupId);
    }

    @GetMapping("/all")
    public Flux<GroupSubjectResponse> findAllByActiveTrue() {
        return groupSubjectService.findAllByActiveTrue();
    }

    @GetMapping("/subject/{subjectId}")
    public Flux<UserResponse> getSubjectTeachers(@PathVariable UUID subjectId) {
        return groupSubjectService.getAllSubjectTeachers(subjectId);
    }
}