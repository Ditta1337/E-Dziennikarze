package com.edziennikarze.gradebook.subject.utils;

import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.teacher.TeacherRepository;
import org.springframework.stereotype.Component;

@Component
public class SubjectTestDatabaseCleaner {

    private final SubjectRepository subjectRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final UserRepository userRepository;

    private final TeacherRepository teacherRepository;

    public SubjectTestDatabaseCleaner(SubjectRepository subjectRepository,
                                      SubjectTaughtRepository subjectTaughtRepository,
                                      UserRepository userRepository, TeacherRepository teacherRepository) {
        this.subjectRepository = subjectRepository;
        this.subjectTaughtRepository = subjectTaughtRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
    }

    public void cleanAll() {
        userRepository.deleteAll().block();
        teacherRepository.deleteAll().block();
        subjectRepository.deleteAll().block();
        subjectTaughtRepository.deleteAll().block();
    }
}
