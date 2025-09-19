package com.edziennikarze.gradebook.lesson.planned.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.UserRepository;

@Component
public class PlannedLessonTestDatabaseCleaner {

    private final PlannedLessonRepository plannedLessonRepository;

    private final UserRepository userRepository;

    private final SubjectRepository subjectRepository;

    private final RoomRepository roomRepository;

    private final GroupRepository groupRepository;

    public PlannedLessonTestDatabaseCleaner(PlannedLessonRepository plannedLessonRepository, UserRepository userRepository,
            SubjectRepository subjectRepository, RoomRepository roomRepository, GroupRepository groupRepository) {
        this.plannedLessonRepository = plannedLessonRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.roomRepository = roomRepository;
        this.groupRepository = groupRepository;
    }

    public void cleanAll() {
        plannedLessonRepository.deleteAll()
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
