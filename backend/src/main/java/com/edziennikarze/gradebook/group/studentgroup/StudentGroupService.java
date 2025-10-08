package com.edziennikarze.gradebook.group.studentgroup;

import java.util.UUID;

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
public class StudentGroupService {

    private final StudentGroupRepository studentGroupRepository;

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    public Mono<StudentGroup> createStudentGroup(Mono<StudentGroup> studentGroupMono) {
        return studentGroupMono.flatMap(studentGroupRepository::save);
    }

    public Flux<Group> getAllStudentGroups(UUID studentId) {
        Flux<UUID> groupIds = studentGroupRepository.findAllByStudentId(studentId)
                .map(StudentGroup::getGroupId);

        return groupRepository.findAllById(groupIds);
    }

    public Flux<UserResponse> getAllGroupStudents(UUID groupId) {
        Flux<UUID> studentIds = studentGroupRepository.findAllByGroupId(groupId)
                .map(StudentGroup::getStudentId);

        return userRepository.findAllById(studentIds)
                .map(UserResponse::from);
    }
}
