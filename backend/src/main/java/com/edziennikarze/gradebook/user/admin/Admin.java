package com.edziennikarze.gradebook.user.admin;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@Data
@Table("admins")
@Builder
public class Admin {
    @Id
    private UUID id;
    private UUID userId;
}
