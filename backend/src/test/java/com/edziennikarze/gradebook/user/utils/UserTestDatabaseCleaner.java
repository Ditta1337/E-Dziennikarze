package com.edziennikarze.gradebook.user.utils;

import com.edziennikarze.gradebook.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserTestDatabaseCleaner {

    private final UserRepository userRepository;

    public UserTestDatabaseCleaner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void cleanAll() {
        userRepository.deleteAll().block();
    }
}
