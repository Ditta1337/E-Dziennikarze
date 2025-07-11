package com.edziennikarze.gradebook.group.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;

@Component
public class GroupTestDatabaseCleaner {

    private final GroupRepository groupRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final UserTestDatabaseCleaner userTestDatabaseCleaner;

    public GroupTestDatabaseCleaner(GroupRepository groupRepository, StudentGroupRepository studentGroupRepository, UserTestDatabaseCleaner userTestDatabaseCleaner) {
        this.groupRepository = groupRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.userTestDatabaseCleaner = userTestDatabaseCleaner;
    }

    public void cleanAll() {
        userTestDatabaseCleaner.cleanAll();
        groupRepository.deleteAll().block();
        studentGroupRepository.deleteAll().block();
    }
}
