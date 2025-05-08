package com.edziennikarze.gradebook.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN,
    GUARDIAN,
    OFFICEWORKER,
    STUDENT,
    TEACHER
}
