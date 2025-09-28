package com.edziennikarze.gradebook.auth.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.AccessDenialException;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.dto.User;

import reactor.core.publisher.Mono;

@Service
public class LoggedInUserService {

    public Mono<Boolean> isSelfOrAllowedRoleElseThrow(UUID userId, Role... allowedRoles) {
        Set<Role> allowedRolesSet = new HashSet<>(Set.of(allowedRoles));
        allowedRolesSet.add(Role.ADMIN);

        return getLoggedInUser()
                .filter(loggedInUser -> allowedRolesSet.contains(loggedInUser.getRole()) || loggedInUser.getId().equals(userId))
                .switchIfEmpty(Mono.error(new AccessDenialException("Access denied")))
                .map(user -> true);
    }

    public Mono<User> getLoggedInUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(User.class);
    }
}
