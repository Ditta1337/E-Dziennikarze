package com.edziennikarze.gradebook.group;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("groups")
public class Group {

    @Id
    private UUID id;

    private int startYear;

    @NotNull
    private String groupCode;

    private boolean isClass;
}
