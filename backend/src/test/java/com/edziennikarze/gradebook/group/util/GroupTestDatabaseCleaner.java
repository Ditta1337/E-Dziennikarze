package com.edziennikarze.gradebook.group.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.group.teachergroup.TeacherGroupRepository;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;

@Component
public class GroupTestDatabaseCleaner {

    private final GroupRepository groupRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final TeacherGroupRepository teacherGroupRepository;

    private final SubjectRepository subjectRepository;

    private final UserTestDatabaseCleaner userTestDatabaseCleaner;

    public GroupTestDatabaseCleaner(GroupRepository groupRepository, StudentGroupRepository studentGroupRepository, UserTestDatabaseCleaner userTestDatabaseCleaner,
            TeacherGroupRepository teacherGroupRepository, SubjectRepository subjectRepository) {
        this.groupRepository = groupRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.userTestDatabaseCleaner = userTestDatabaseCleaner;
        this.teacherGroupRepository = teacherGroupRepository;
        this.subjectRepository = subjectRepository;
    }

    public void cleanAll() {
        userTestDatabaseCleaner.cleanAll();
        groupRepository.deleteAll().block();
        studentGroupRepository.deleteAll().block();
        subjectRepository.deleteAll().block();
        teacherGroupRepository.deleteAll().block();
    }
}
