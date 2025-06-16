package com.edziennikarze.gradebook.subject.utils;

import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.utils.UserTestDatabaseCleaner;
import org.springframework.stereotype.Component;

@Component
public class SubjectTestDatabaseCleaner {

    private final SubjectRepository subjectRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final UserTestDatabaseCleaner userTestDatabaseCleaner;


    public SubjectTestDatabaseCleaner(SubjectRepository subjectRepository,
                                      SubjectTaughtRepository subjectTaughtRepository,
                                      UserTestDatabaseCleaner userTestDatabaseCleaner) {
        this.subjectRepository = subjectRepository;
        this.subjectTaughtRepository = subjectTaughtRepository;
        this.userTestDatabaseCleaner = userTestDatabaseCleaner;
    }

    public void cleanAll() {
        userTestDatabaseCleaner.cleanAll();
        subjectRepository.deleteAll().block();
        subjectTaughtRepository.deleteAll().block();
    }
}
