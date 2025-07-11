package com.edziennikarze.gradebook.group.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.group.GroupRepository;

@Component
public class GroupTestDatabaseCleaner {

    private final GroupRepository groupRepository;

    public GroupTestDatabaseCleaner(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void cleanAll() {
        groupRepository.deleteAll().block();
    }
}
