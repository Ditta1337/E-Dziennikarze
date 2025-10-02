package com.edziennikarze.gradebook.group;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("groups")
public class Group {

    @Id
    private UUID id;

    private int startYear;

    @NotNull
    private String groupCode;

    private boolean isClass;
}
