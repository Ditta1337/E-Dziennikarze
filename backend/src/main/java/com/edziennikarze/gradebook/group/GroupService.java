package com.edziennikarze.gradebook.group;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.edziennikarze.gradebook.exception.ParseException;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final StudentGroupRepository studentGroupRepository;

    public Mono<Group> createGroup(Mono<Group> groupMono) {
        return groupMono.flatMap(groupRepository::save);
    }

    public Flux<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Flux<Group> getAllClasses() {
        return groupRepository.findAllByIsClass(true);
    }

    public Flux<Group> getAllByStartYear(int startYear) {
        return groupRepository.findAllByStartYear(startYear);
    }

    public Flux<Group> incrementAllGroupsCode() {
        return groupRepository.findAll()
                .map(this::updateGroupCode)
                .flatMap(groupRepository::save);
    }

    public Mono<Group> updateGroup(Mono<Group> groupMono) {
        return groupMono.flatMap(group -> groupRepository.findById(group.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Group with id " + group.getId() + " not found")))
                .flatMap(existingGroup -> {
                    existingGroup.setGroupCode(group.getGroupCode());
                    existingGroup.setStartYear(group.getStartYear());
                    existingGroup.setClass(group.isClass());
                    return groupRepository.save(existingGroup);
                }));
    }

    public Mono<Void> deleteGroup(UUID groupId) {
        return groupRepository.deleteById(groupId);
    }

    private Group updateGroupCode(Group group) {
        String newCode = replaceCurrentCodeWithIncrementedYear(group.getGroupCode());
        group.setGroupCode(newCode);
        return group;
    }

    private String replaceCurrentCodeWithIncrementedYear(String currentCode) {
        List<String> parts = Arrays.asList(currentCode.split("_"));
        if (parts.size() != 2) {
            throw new ParseException("Cannot parse group code: " + currentCode);
        }
        Integer parsedYear = tryParseYearFromGroupCode(parts.get(0));
        return String.format("%s_%s", parsedYear + 1, parts.get(1));
    }

    private int tryParseYearFromGroupCode(String yearPart) {
        try {
            return Integer.parseInt(yearPart);
        } catch (NumberFormatException e) {
            throw new ParseException("Cannot parse year from group code: " + yearPart);
        }
    }
}
