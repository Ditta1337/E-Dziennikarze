package com.edziennikarze.gradebook.plan.calculation.dto;

import com.edziennikarze.gradebook.exception.MarshallException;
import com.edziennikarze.gradebook.exception.UnmarshallException;
import com.edziennikarze.gradebook.lesson.planned.PlannedLesson;
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
@Table("plan_calculations")
public class PlanCalculation {

    @Id
    private UUID id;

    @NotNull
    private String name;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String calculation;

    public void setCalculation(List<PlannedLesson> plannedLessons, ObjectMapper objectMapper) {
        try {
            this.calculation = objectMapper.writeValueAsString(plannedLessons);
        } catch (JsonProcessingException e) {
            throw new MarshallException("Failed to convert PlannedLessons to JSON string");
        }
    }

    public List<PlannedLesson> getCalculation(ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(calculation, new TypeReference<List<PlannedLesson>>() {});
        } catch (JsonProcessingException e) {
            throw new UnmarshallException("Failed to convert JSON string to PlannedLessons object");
        }
    }
}
