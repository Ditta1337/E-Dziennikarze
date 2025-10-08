package com.edziennikarze.gradebook.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("subjects")
public class Subject {

    @Id
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private int maxLessonsPerDay;

    @NotNull
    private int lessonsPerWeek;
}
