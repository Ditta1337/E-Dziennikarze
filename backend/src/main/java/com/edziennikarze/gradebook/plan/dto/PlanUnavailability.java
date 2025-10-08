package com.edziennikarze.gradebook.plan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanUnavailability {

    private int day;

    private int lesson;
}
