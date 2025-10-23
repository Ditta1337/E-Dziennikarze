package com.edziennikarze.gradebook.plan.calculation;

import com.edziennikarze.gradebook.plan.calculation.dto.PlanCalculationsSummary;
import com.edziennikarze.gradebook.plan.calculation.dto.request.PlanCalculationRequest;
import com.edziennikarze.gradebook.plan.calculation.dto.request.PlanCalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/plan/calculation")
@RequiredArgsConstructor
public class PlanCalculationController {

    private final PlanCalculationService planCalculationService;

    @PostMapping
    public Mono<PlanCalculationResponse> savePlanCalculation(@RequestBody Mono<PlanCalculationRequest> planCalculationRequestMono) {
        return planCalculationService.savePlanCalculation(planCalculationRequestMono);
    }

    @GetMapping("/plan/{id}")
    public Mono<PlanCalculationResponse> getAllPlanCalculationsForPlan(@PathVariable UUID id) {
        return planCalculationService.getAllPlanCalculationsForPlan(id);
    }

    @GetMapping("/summary/{planId}")
    public Flux<PlanCalculationsSummary> getPlanCalculationsSummary(@PathVariable UUID planId) {
        return planCalculationService.getPlanCalculationsSummary(planId);
    }
}
