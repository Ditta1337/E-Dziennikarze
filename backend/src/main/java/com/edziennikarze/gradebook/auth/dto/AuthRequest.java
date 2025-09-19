package com.edziennikarze.gradebook.auth.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AuthRequest {

    @NotNull
    private String email;

    @NotNull
    private String password;
}
