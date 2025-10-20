package com.edziennikarze.gradebook.solver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalFunction {

    @NotNull
    private String functionName;

    @NotNull
    private String name;

    @NotNull
    private String description;

}
