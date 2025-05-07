package com.edziennikarze.gradebook.user.teacher;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("teachers")
public class Teacher {
    @Id
    private UUID id;
    private UUID userId;
}
