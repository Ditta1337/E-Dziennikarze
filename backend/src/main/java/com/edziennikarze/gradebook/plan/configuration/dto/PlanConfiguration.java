package com.edziennikarze.gradebook.plan.configuration.dto;

import com.edziennikarze.gradebook.plan.dto.Plan;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("plan_configurations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanConfiguration {

    @Id
    private UUID id;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String name;

    private UUID officeWorkerId;

    private boolean calculated;

    @Column("configuration")
    private String configuration;

    public void setConfigurationObject(Plan plan, ObjectMapper objectMapper) {
        try {
            this.configuration = objectMapper.writeValueAsString(plan);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert plan to JSON string", e);
        }
    }

    public Plan getConfigurationObject(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(configuration, Plan.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON string to Plan object", e);
        }
    }
}


