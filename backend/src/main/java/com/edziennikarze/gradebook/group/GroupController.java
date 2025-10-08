package com.edziennikarze.gradebook.group;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public Mono<Group> createGroup(@RequestBody Mono<Group> groupMono) {
        return groupService.createGroup(groupMono);
    }

    @GetMapping("/all")
    public Flux<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/all/classes")
    public Flux<Group> getAllClasses() {
        return groupService.getAllClasses();
    }

    @GetMapping("/all/{startingYear}")
    public Flux<Group> getAllGroupsStartYear(@PathVariable int startingYear) {
        return groupService.getAllByStartYear(startingYear);
    }

    @PutMapping
    public Mono<Group> updateGroup(@RequestBody Mono<Group> groupMono) {
        return groupService.updateGroup(groupMono);
    }

    @PatchMapping("/increment")
    public Flux<Group> incrementAllGroupsCode() {
        return groupService.incrementAllGroupsCode();
    }

    @DeleteMapping("/{groupId}")
    public Mono<Void> deleteGroup(@PathVariable UUID groupId) {
        return groupService.deleteGroup(groupId);
    }
}