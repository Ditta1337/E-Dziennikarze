package com.edziennikarze.gradebook.plan.configuration;

import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationResponse;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/plan/configuration")
@RequiredArgsConstructor
public class PlanConfigurationController {

    private final PlanConfigurationService planConfigurationService;

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public Mono<PlanConfigurationResponse> createPlanConfiguration(@RequestBody Mono<String> nameMono) {
        return planConfigurationService.createPlanConfiguration(nameMono);
    }

    @PostMapping(path = "/copy/{planConfigurationId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public Mono<PlanConfigurationResponse> copyPlanConfiguration(@PathVariable UUID planConfigurationId, @RequestBody Mono<String> nameMono) {
        return planConfigurationService.copyPlanConfiguration(planConfigurationId, nameMono);
    }

    @GetMapping("/{planConfigurationId}")
    public Mono<PlanConfigurationResponse> getPlanConfiguration(@PathVariable UUID planConfigurationId) {
        return planConfigurationService.getPlanConfiguration(planConfigurationId);
    }

    @GetMapping("/summary/all")
    public Flux<PlanConfigurationSummary> getAllPlanConfigurationSummary() {
        return planConfigurationService.getPlanConfigurationSummaryList();
    }

    @PutMapping
    public Mono<PlanConfigurationResponse> updatePlanConfiguration(@RequestBody Mono<PlanConfigurationResponse> planConfigurationMono) {
        return planConfigurationService.updatePlanConfiguration(planConfigurationMono);
    }
}
