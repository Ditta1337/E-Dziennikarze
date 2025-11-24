package com.edziennikarze.gradebook.plan.manual;

import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLessonResponse;
import com.edziennikarze.gradebook.plan.manual.dto.ManualPlanResponse;
import com.edziennikarze.gradebook.plan.manual.dto.ManualPlanSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plan/manual")
@RequiredArgsConstructor
public class ManualPlanController {

    private final ManualPlanService manualPlanService;


    @PostMapping(path = "copy/calculation/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public Mono<UUID> copyCalculationToManualPlan(@PathVariable UUID id, @RequestBody String name) {
        return manualPlanService.copyCalculationToManualPlan(id, name);
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public Mono<UUID> createEmptyManualPlan(@RequestBody String name) {
        return manualPlanService.createEmptyManualPlan(name);
    }

    @GetMapping("{id}")
    public Mono<ManualPlanResponse> getManualPlan(@PathVariable UUID id) {
        return manualPlanService.getManualPlan(id);
    }

    @PutMapping("/{id}")
    public Mono<ManualPlanResponse> saveManualPlan(@PathVariable UUID id, @RequestBody List<PlannedLessonResponse> manualPlan) {
        return manualPlanService.saveManualPlan(id, manualPlan);
    }

    @GetMapping("/summary")
    public Flux<ManualPlanSummary> getManualPlanSummary() {
        return manualPlanService.getManualPlanSummary();
    }

}
