package com.edziennikarze.gradebook.subject;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@Table("subjects")
public class Subject {

    @Id
    private UUID id;

    @NotNull
    private String name;
}
