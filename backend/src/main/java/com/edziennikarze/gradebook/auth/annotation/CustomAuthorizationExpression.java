package com.edziennikarze.gradebook.auth.annotation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.user.Role;

@Component("customAuthorizationExpression")
public class CustomAuthorizationExpression {

    public boolean hasAnyRole(Authentication authentication, Role... roles) {
        if ( authentication == null || !authentication.isAuthenticated() ) {
            return false;
        }

        Set<String> requiredRoles = Arrays.stream(roles)
                .map(Role::name)
                .collect(Collectors.toSet());

        return authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .anyMatch(requiredRoles::contains);
    }
}
