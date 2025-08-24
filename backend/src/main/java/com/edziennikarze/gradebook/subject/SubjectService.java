package com.edziennikarze.gradebook.subject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.edziennikarze.gradebook.attendance.AttendanceRepository;
import com.edziennikarze.gradebook.subject.subjecttaught.SubjectTaughtRepository;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    private final SubjectTaughtRepository subjectTaughtRepository;

    private final AttendanceRepository attendanceRepository;

    public Mono<Subject> createSubject(Mono<Subject> subjectMono) {
        return subjectMono.flatMap(subjectRepository::save);
    }

    public Flux<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Mono<Void> deleteSubject(UUID subjectId) {
        return subjectRepository.deleteById(subjectId);
    }
}
