package com.edziennikarze.gradebook.group;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/group")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Group> createGroup(@RequestBody Mono<Group> groupMono) {
        return groupService.createGroup(groupMono);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/all/classes")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Group> getAllClasses() {
        return groupService.getAllClasses();
    }

    @GetMapping("/all/{startingYear}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Group> getAllGroupsStartYear(@PathVariable int startingYear) {
        return groupService.getAllByStartYear(startingYear);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Group> updateGroup(@RequestBody Mono<Group> groupMono) {
        return groupService.updateGroup(groupMono);
    }

    @PatchMapping("/increment")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Flux<Group> incrementAllGroupsStartYear() {
        return groupService.incrementAllGroupsStartYear();
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Void> deleteGroup(@PathVariable UUID groupId) {
        return groupService.deleteGroup(groupId);
    }
}