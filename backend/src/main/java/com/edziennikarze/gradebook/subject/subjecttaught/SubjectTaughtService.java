package com.edziennikarze.gradebook.subject.subjecttaught;

import com.edziennikarze.gradebook.subject.subjecttaught.dto.SubjectTaught;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.subject.Subject;
import com.edziennikarze.gradebook.subject.SubjectRepository;
import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.UserRepository;

@Service
@RequiredArgsConstructor
public class SubjectTaughtService {

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final SubjectRepository subjectRepository;

    private final UserRepository userRepository;

    public Mono<SubjectTaught> createSubjectTaught(Mono<SubjectTaught> subjectTaughtMono) {
        return subjectTaughtMono.flatMap(subjectTaughtRepository::save);
    }

    public Flux<SubjectTaught> createSubjectsTaught(Flux<SubjectTaught> subjectTaughtFlux) {
        return subjectTaughtRepository.saveAll(subjectTaughtFlux);
    }

    public Flux<Subject> getAllSubjectsTaught() {
        Flux<UUID> subjectIds = subjectTaughtRepository.findAll()
                .map(SubjectTaught::getSubjectId);

        return subjectRepository.findAllById(subjectIds);
    }

    public Flux<Subject> getSubjectsTaughtByTeacher(UUID teacherId) {
        Flux<UUID> subjectIds = subjectTaughtRepository.findAllByTeacherId(teacherId)
                .map(SubjectTaught::getSubjectId);

        return subjectRepository.findAllById(subjectIds);
    }

    public Flux<User> getTeachersTeachingSubject(UUID subjectId) {
        Flux<UUID> teacherIds = subjectTaughtRepository.findAllBySubjectId(subjectId)
                .map(SubjectTaught::getTeacherId);

        return userRepository.findAllById(teacherIds);
    }

    public Mono<Void> deleteByTeacherIdAndSubjectId(UUID teacherId, UUID subjectId) {
        return subjectTaughtRepository.deleteByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}
