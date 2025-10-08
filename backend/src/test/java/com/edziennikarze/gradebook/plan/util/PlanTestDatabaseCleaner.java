package com.edziennikarze.gradebook.plan.util;

import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.group.studentgroup.StudentGroupRepository;
import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import com.edziennikarze.gradebook.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class PlanTestDatabaseCleaner {

    private final GroupRepository groupRepository;

    private final StudentGroupRepository studentGroupRepository;

    private final UserRepository userRepository;

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    public PlanTestDatabaseCleaner(GroupRepository groupRepository, StudentGroupRepository studentGroupRepository, UserRepository userRepository, TeacherUnavailabilityRepository teacherUnavailabilityRepository) {
        this.groupRepository = groupRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.userRepository = userRepository;
        this.teacherUnavailabilityRepository = teacherUnavailabilityRepository;
    }

    public void cleanAll() {
        groupRepository.deleteAll()
                .block();
        studentGroupRepository.deleteAll()
                .block();
        userRepository.deleteAll()
                .block();
        teacherUnavailabilityRepository.deleteAll()
                .block();
    }
}
