package com.edziennikarze.gradebook.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Table("users")
public class User {
    @Id
    private UUID id;
    private String name;
    private String surname;
    private LocalDate createdAt;
    private String address;
    private String email;
    private String password;
    private String contact;
    private String imageBase64;
    private Role role;
    private Boolean isActive;
}
