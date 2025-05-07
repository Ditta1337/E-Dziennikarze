package com.edziennikarze.gradebook.user.teacher;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TeacherRepository extends ReactiveCrudRepository<Teacher, UUID> {
}
