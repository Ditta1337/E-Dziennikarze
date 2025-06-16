package com.edziennikarze.gradebook.user.utils;

import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.studentguardian.StudentGuardianRepository;
import org.springframework.stereotype.Component;

@Component
public class UserTestDatabaseCleaner {

    private final UserRepository userRepository;

    private final StudentGuardianRepository studentGuardianRepository;

    public UserTestDatabaseCleaner(UserRepository userRepository, StudentGuardianRepository studentGuardianRepository) {
        this.userRepository = userRepository;
        this.studentGuardianRepository = studentGuardianRepository;
    }

    public void cleanAll() {
        userRepository.deleteAll().block();
        studentGuardianRepository.deleteAll().block();
    }
}
