package com.edziennikarze.gradebook.group.teachergroup;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.group.Group;
import com.edziennikarze.gradebook.group.GroupRepository;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.UserResponse;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TeacherGroupService {

    private final TeacherGroupRepository teacherGroupRepository;

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    public Mono<TeacherGroup> createTeacherGroup(Mono<TeacherGroup> teacherGroupMono) {
        return teacherGroupMono.flatMap(teacherGroupRepository::save);
    }

    public Flux<Group> getAllTeacherGroups(UUID teacherId) {
        Flux<UUID> groupIds = teacherGroupRepository.findAllByTeacherId(teacherId)
                .map(TeacherGroup::getGroupId);

        return groupRepository.findAllById(groupIds);
    }

    public Flux<UserResponse> getAllGroupTeachers(UUID groupId) {
        Flux<UUID> teacherIds = teacherGroupRepository.findAllByGroupId(groupId)
                .map(TeacherGroup::getTeacherId);

        return userRepository.findAllById(teacherIds)
                .map(UserResponse::from);
    }

    public Flux<UserResponse> getAllSubjectTeachers(UUID subjectId) {
        Flux<UUID> teachersIds = teacherGroupRepository.findAllBySubjectId(subjectId)
                .map(TeacherGroup::getTeacherId);

        return userRepository.findAllById(teachersIds)
                .map(UserResponse::from);
    }
}
