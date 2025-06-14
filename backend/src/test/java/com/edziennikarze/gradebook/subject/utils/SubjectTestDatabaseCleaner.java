package com.edziennikarze.gradebook.subject.utils;

import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class SubjectTestDatabaseCleaner {

    private final SubjectRepository subjectRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final UserRepository userRepository;


    public SubjectTestDatabaseCleaner(SubjectRepository subjectRepository,
                                      SubjectTaughtRepository subjectTaughtRepository,
                                      UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.subjectTaughtRepository = subjectTaughtRepository;
        this.userRepository = userRepository;
    }

    public void cleanAll() {
        userRepository.deleteAll().block();
        subjectRepository.deleteAll().block();
        subjectTaughtRepository.deleteAll().block();
    }
}
