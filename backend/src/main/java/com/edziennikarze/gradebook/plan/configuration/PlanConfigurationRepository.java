package com.edziennikarze.gradebook.plan.configuration;

import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfiguration;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationSummary;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PlanConfigurationRepository extends ReactiveCrudRepository<PlanConfiguration, UUID> {

    @Query("""
                SELECT
                            p.id AS id,
                            p.created_at AS created_at,
                            p.name AS name,
                            p.office_worker_id AS office_worker_id,
                            u.name AS office_worker_name,
                            u.surname AS office_worker_surname
                        FROM plan_configurations p
                        INNER JOIN users u ON p.office_worker_id = u.id
            """)
    Flux<PlanConfigurationSummary> findAllSummary();

}
