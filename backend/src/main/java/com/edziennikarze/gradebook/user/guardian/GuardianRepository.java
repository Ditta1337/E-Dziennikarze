package com.edziennikarze.gradebook.user.guardian;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface GuardianRepository extends ReactiveCrudRepository<Guardian, UUID> {
}
