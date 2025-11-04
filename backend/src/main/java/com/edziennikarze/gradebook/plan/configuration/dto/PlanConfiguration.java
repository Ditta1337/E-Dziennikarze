package com.edziennikarze.gradebook.plan.configuration.dto;

import com.edziennikarze.gradebook.exception.MarshallException;
import com.edziennikarze.gradebook.exception.UnmarshallException;
import com.edziennikarze.gradebook.plan.dto.Plan;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("plan_configurations")
public class PlanConfiguration {

    @Id
    private UUID id;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private String name;

    @NotNull
    private UUID officeWorkerId;

    private boolean calculated;

    @Column("configuration")
    private String configuration;

    public void setConfiguration(Plan plan, ObjectMapper objectMapper) {
        try {
            this.configuration = objectMapper.writeValueAsString(plan);
        } catch (JsonProcessingException e) {
            throw new MarshallException("Failed to convert Plan to JSON string");
        }
    }

    public Plan getConfiguration(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(configuration, Plan.class);
        } catch (JsonProcessingException e) {
            throw new UnmarshallException("Failed to convert JSON string to Plan object");
        }
    }

    public static PlanConfiguration from(String configuration, String name, UUID officeWorkerId) {
        return PlanConfiguration.builder()
                .name(name)
                .officeWorkerId(officeWorkerId)
                .calculated(false)
                .configuration(configuration)
                .build();
    }

}


