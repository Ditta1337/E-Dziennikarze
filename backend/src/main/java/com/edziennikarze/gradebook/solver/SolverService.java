package com.edziennikarze.gradebook.solver;

import com.edziennikarze.gradebook.plan.dto.Plan;
import com.edziennikarze.gradebook.plan.dto.PlanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolverService {

    private final WebClient solverWebClient;

    public Mono<PlanResponse> calculatePlan(Plan planRequest) {
        log.info("Sending request to solver API for problem: {}", planRequest.toString());

        return solverWebClient.post()
                .uri("/solve")
                .bodyValue(planRequest)
                .retrieve()
                .bodyToMono(PlanResponse.class)
                .doOnError(e -> log.error("Error calling solver API", e))
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Solver API returned an error status: {} with body {}", e.getStatusCode(), e.getResponseBodyAsString());
                    return Mono.empty();
                });
    }


}