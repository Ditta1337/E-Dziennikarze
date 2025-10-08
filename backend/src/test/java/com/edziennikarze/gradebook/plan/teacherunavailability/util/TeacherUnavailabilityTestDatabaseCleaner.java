package com.edziennikarze.gradebook.plan.teacherunavailability.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.plan.teacherunavailability.TeacherUnavailabilityRepository;
import com.edziennikarze.gradebook.user.UserRepository;

@Component
public class TeacherUnavailabilityTestDatabaseCleaner {

    private final UserRepository userRepository;

    private final TeacherUnavailabilityRepository teacherUnavailabilityRepository;

    public TeacherUnavailabilityTestDatabaseCleaner(UserRepository userRepository, TeacherUnavailabilityRepository teacherUnavailabilityRepository) {
        this.userRepository = userRepository;
        this.teacherUnavailabilityRepository = teacherUnavailabilityRepository;
    }

    public void cleanAll() {
        userRepository.deleteAll()
                .block();
        teacherUnavailabilityRepository.deleteAll()
                .block();
    }
}
