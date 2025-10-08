package com.edziennikarze.gradebook.group.groupsubject;

import java.util.UUID;

import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubject;
import com.edziennikarze.gradebook.group.groupsubject.dto.GroupSubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.UserResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GroupSubjectService {

    private final GroupSubjectRepository groupSubjectRepository;

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    public Mono<GroupSubject> createTeacherGroup(Mono<GroupSubject> teacherGroupMono) {
        return teacherGroupMono.flatMap(groupSubjectRepository::save);
    }

    public Flux<GroupSubjectResponse> findAllByActiveTrue() {
        return groupSubjectRepository.findAllByActiveTrue();
    }

    public Flux<Group> getAllTeacherGroups(UUID teacherId) {
        Flux<UUID> groupIds = groupSubjectRepository.findAllByTeacherId(teacherId)
                .map(GroupSubject::getGroupId);

        return groupRepository.findAllById(groupIds);
    }

    public Flux<UserResponse> getAllGroupTeachers(UUID groupId) {
        Flux<UUID> teacherIds = groupSubjectRepository.findAllByGroupId(groupId)
                .map(GroupSubject::getTeacherId);

        return userRepository.findAllById(teacherIds)
                .map(UserResponse::from);
    }

    public Flux<UserResponse> getAllSubjectTeachers(UUID subjectId) {
        Flux<UUID> teachersIds = groupSubjectRepository.findAllBySubjectId(subjectId)
                .map(GroupSubject::getTeacherId);

        return userRepository.findAllById(teachersIds)
                .map(UserResponse::from);
    }
}
