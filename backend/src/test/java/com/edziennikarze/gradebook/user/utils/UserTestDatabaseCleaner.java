package com.edziennikarze.gradebook.user.utils;

import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.admin.AdminRepository;
import com.edziennikarze.gradebook.user.guardian.GuardianRepository;
import com.edziennikarze.gradebook.user.officeworker.OfficeWorkerRepository;
import com.edziennikarze.gradebook.user.student.StudentRepository;
import com.edziennikarze.gradebook.user.teacher.TeacherRepository;
import org.springframework.stereotype.Component;

@Component
public class UserTestDatabaseCleaner {
    private final AdminRepository adminRepo;
    private final GuardianRepository guardianRepo;
    private final StudentRepository studentRepo;
    private final OfficeWorkerRepository officeWorkerRepo;
    private final TeacherRepository teacherRepo;
    private final UserRepository userRepo;

    public UserTestDatabaseCleaner(AdminRepository a, GuardianRepository g, StudentRepository s,
                                   OfficeWorkerRepository o, TeacherRepository t,
                                   UserRepository u) {
        this.adminRepo = a;
        this.guardianRepo = g;
        this.studentRepo  = s;
        this.officeWorkerRepo = o;
        this.teacherRepo  = t;
        this.userRepo     = u;
    }

    public void cleanAll() {
        adminRepo.deleteAll().block();
        studentRepo.deleteAll().block();
        guardianRepo.deleteAll().block();
        officeWorkerRepo.deleteAll().block();
        teacherRepo.deleteAll().block();
        userRepo.deleteAll().block();
    }
}
