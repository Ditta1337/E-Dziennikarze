package com.edziennikarze.gradebook.group;

import static com.edziennikarze.gradebook.user.Role.*;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER})
    public Mono<Group> createGroup(@RequestBody Mono<Group> groupMono) {
        return groupService.createGroup(groupMono);
    }

    @GetMapping("/all")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/all/classes")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<Group> getAllClasses() {
        return groupService.getAllClasses();
    }

    @GetMapping("/all/{startingYear}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<Group> getAllGroupsStartYear(@PathVariable int startingYear) {
        return groupService.getAllByStartYear(startingYear);
    }

    @PutMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER})
    public Mono<Group> updateGroup(@RequestBody Mono<Group> groupMono) {
        return groupService.updateGroup(groupMono);
    }

    @PatchMapping("/increment")
    @HasAnyRole({ADMIN, OFFICE_WORKER})
    public Flux<Group> incrementAllGroupsStartYear() {
        return groupService.incrementAllGroupsStartYear();
    }

    @DeleteMapping("/{groupId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER})
    public Mono<Void> deleteGroup(@PathVariable UUID groupId) {
        return groupService.deleteGroup(groupId);
    }
}
