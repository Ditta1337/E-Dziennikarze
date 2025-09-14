package com.edziennikarze.gradebook.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Target;

import com.edziennikarze.gradebook.user.Role;

public @interface AuthorizationAnnotation {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("@customAuthorizationExpression.hasAnyRole(authentication, #value)")
    @interface HasAnyRole {
        Role[] value();
    }

}
