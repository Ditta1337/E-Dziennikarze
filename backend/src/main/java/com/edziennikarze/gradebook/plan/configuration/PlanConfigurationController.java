package com.edziennikarze.gradebook.plan.configuration;

import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfiguration;
import com.edziennikarze.gradebook.plan.configuration.dto.PlanConfigurationResponse;
import com.edziennikarze.gradebook.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/plan/configuration")
@RequiredArgsConstructor
public class PlanConfigurationController {

    private final PlanConfigurationService planConfigurationService;

    @PostMapping()
    public Mono<PlanConfiguration> createPlanConfiguration(@RequestBody Mono<String> nameMono) {
        return planConfigurationService.createPlanConfiguration(nameMono);
    }

    @GetMapping("/{planConfigurationId}")
    public Mono<PlanConfigurationResponse> getPlanConfiguration(@PathVariable UUID planConfigurationId) {
        return planConfigurationService.getPlanConfiguration(planConfigurationId);
    }
}
