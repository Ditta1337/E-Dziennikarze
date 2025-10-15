package com.edziennikarze.gradebook.plan;

import com.edziennikarze.gradebook.plan.dto.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping("/enqueue")
    public Mono<Plan> initializePlan(@RequestBody Mono<Plan> planMono) {
        return planService.initializePlan(planMono);
    }
}
