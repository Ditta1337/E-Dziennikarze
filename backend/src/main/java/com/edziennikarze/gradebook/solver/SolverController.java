package com.edziennikarze.gradebook.solver;

import com.edziennikarze.gradebook.solver.dto.GoalFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/solver")
@RequiredArgsConstructor
public class SolverController {

    private final SolverService solverService;

    @GetMapping("/goal/functions")
    public Flux<GoalFunction> getGoalFunctions() {
        return solverService.getGoalFunctions();
    }

}
