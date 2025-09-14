package com.edziennikarze.gradebook.user.dto;

import com.edziennikarze.gradebook.user.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private String name;
    private String surname;
    private LocalDate createdAt;
    private String address;
    private String email;
    private String contact;
    private String imageBase64;
    private Role role;
    private boolean active;
    private boolean choosingPreferences;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .createdAt(user.getCreatedAt())
                .address(user.getAddress())
                .email(user.getEmail())
                .contact(user.getContact())
                .imageBase64(user.getImageBase64())
                .role(user.getRole())
                .active(user.isActive())
                .choosingPreferences(user.isChoosingPreferences())
                .build();
    }
}

