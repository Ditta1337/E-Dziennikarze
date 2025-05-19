package com.edziennikarze.gradebook.user.admin;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AdminRepository extends ReactiveCrudRepository<Admin, UUID> {
}
