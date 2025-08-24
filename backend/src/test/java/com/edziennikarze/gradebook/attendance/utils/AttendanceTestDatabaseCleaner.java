package com.edziennikarze.gradebook.attendance.utils;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.attendance.AttendanceRepository;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.UserRepository;

@Component
public class AttendanceTestDatabaseCleaner {

    private final AttendanceRepository attendanceRepository;

    private final UserRepository userRepository;

    private final SubjectRepository subjectRepository;

    private final RoomRepository roomRepository;

    private final GroupRepository groupRepository;

    public AttendanceTestDatabaseCleaner(UserRepository userRepository, SubjectRepository subjectRepository, AttendanceRepository attendanceRepository,
            RoomRepository roomRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.roomRepository = roomRepository;
        this.groupRepository = groupRepository;
    }

    public void cleanAll() {
        attendanceRepository.deleteAll()
                .block();
        userRepository.deleteAll()
                .block();
        subjectRepository.deleteAll()
                .block();
        roomRepository.deleteAll()
                .block();
        groupRepository.deleteAll()
                .block();
    }
}
