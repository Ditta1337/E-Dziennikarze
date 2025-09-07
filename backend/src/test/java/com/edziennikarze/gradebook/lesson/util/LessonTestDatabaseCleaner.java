package com.edziennikarze.gradebook.lesson.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.lesson.assigned.AssignedLessonRepository;
import com.edziennikarze.gradebook.lesson.planned.PlannedLessonRepository;
import com.edziennikarze.gradebook.room.RoomRepository;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.UserRepository;

@Component
public class LessonTestDatabaseCleaner {

    private final AssignedLessonRepository assignedLessonRepository;

    private final PlannedLessonRepository plannedLessonRepository;

    private final UserRepository userRepository;

    private final SubjectRepository subjectRepository;

    private final RoomRepository roomRepository;

    private final GroupRepository groupRepository;

    public LessonTestDatabaseCleaner(AssignedLessonRepository assignedLessonRepository, UserRepository userRepository,
            SubjectRepository subjectRepository, RoomRepository roomRepository, GroupRepository groupRepository,
            PlannedLessonRepository plannedLessonRepository) {
        this.assignedLessonRepository = assignedLessonRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.roomRepository = roomRepository;
        this.groupRepository = groupRepository;
        this.plannedLessonRepository = plannedLessonRepository;
    }

    public void cleanAll() {
        assignedLessonRepository.deleteAll()
                .block();
        userRepository.deleteAll()
                .block();
        subjectRepository.deleteAll()
                .block();
        roomRepository.deleteAll()
                .block();
        groupRepository.deleteAll()
                .block();
        plannedLessonRepository.deleteAll()
                .block();
    }
}
