package com.edziennikarze.gradebook.plan.configuration;

import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfiguration;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlanConfigurationRepository extends ReactiveCrudRepository<PlanConfiguration, UUID> {
}
