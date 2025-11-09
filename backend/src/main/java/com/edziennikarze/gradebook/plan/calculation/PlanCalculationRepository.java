package com.edziennikarze.gradebook.plan.calculation;

import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculation;
import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculationsSummary;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PlanCalculationRepository extends ReactiveCrudRepository<PlanCalculation, UUID> {

    Flux<PlanCalculation> findAllById(@NotNull UUID id);

    @Query("""
            SELECT
                pc.id as id,
                pc.name as plan_name,
                pc.created_at as created_at,
                pc.goals as goals
            FROM plan_calculations pc
            WHERE pc.plan_id = :planId
            """)
    Flux<PlanCalculationsSummary> findAllSummaryByPlanId(UUID planId);

}
