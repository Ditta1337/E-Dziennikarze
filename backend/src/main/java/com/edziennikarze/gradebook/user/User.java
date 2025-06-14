package com.edziennikarze.gradebook.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Table("users")
public class User {

    @Id
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private LocalDate createdAt;

    @NotNull
    private String address;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String contact;

    private String imageBase64;

    @NotNull
    private Role role;

    private boolean isActive;

    private boolean isChoosingPreferences;
}
