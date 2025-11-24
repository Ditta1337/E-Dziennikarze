package com.edziennikarze.gradebook.plan.manual.dto;

import com.edziennikarze.gradebook.exception.UnmarshallException;
import com.edziennikarze.gradebook.lesson.planned.dto.PlannedLesson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("manual_plans")
public class ManualPlan {

    @Id
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private UUID officeWorkerId;

    @Builder.Default
    private UUID planCalculationId = null;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private String plan;

    @Builder.Default
    private String errors = null;

    public List<PlannedLesson> getPlan(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(plan, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new UnmarshallException("Failed to convert JSON string to PlannedLessons object");
        }
    }

    public void setPlan(List<PlannedLesson> plannedLessons, ObjectMapper objectMapper) {
        try {
            String plan = objectMapper.writeValueAsString(plannedLessons);
            setPlan(plan);
        } catch (JsonProcessingException e) {
            throw new UnmarshallException("Failed to convert PlannedLessons to string");
        }
    }

    public List<String> getErrors(ObjectMapper objectMapper) {
        if(errors == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(errors, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new UnmarshallException("Failed to convert JSON string to List<String>");
        }
    }

    public void setErrors(List<String> errors, ObjectMapper objectMapper) {
        try {
            String stringifiedErrors = objectMapper.writeValueAsString(errors);
            setErrors(stringifiedErrors);
        } catch (JsonProcessingException e) {
            throw new UnmarshallException("Failed to convert List<String> errors to string");
        }
    }
}
