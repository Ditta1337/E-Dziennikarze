package com.edziennikarze.gradebook.plan.calculation;

import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculation;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PlanCalculationRepository extends ReactiveCrudRepository<PlanCalculation, UUID> {

    Flux<PlanCalculation> findAllByPlanId(@NotNull UUID planId);
}
