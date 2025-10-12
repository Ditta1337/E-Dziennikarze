package com.edziennikarze.gradebook.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("properties")
public class Property {

    @Id
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private PropertyType type;

    @NotNull
    private String defaultValue;

    private Object value;

    boolean saveToFetch;
}
