package com.edziennikarze.gradebook.auth.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.edziennikarze.gradebook.user.studentguardian.StudentGuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.AccessDenialException;
import com.edziennikarze.gradebook.user.Role;
import com.edziennikarze.gradebook.user.dto.User;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoggedInUserService {

    private final StudentGuardianRepository studentGuardianRepository;

    public Mono<Boolean> isSelfOrAllowedRoleElseThrow(UUID userId, Role... allowedRoles) {
        Set<Role> allowedRolesSet = new HashSet<>(Set.of(allowedRoles));
        allowedRolesSet.add(Role.ADMIN);

        return getLoggedInUser()
                .flatMap(loggedInUser -> {
                    boolean isSelfOrAllowed = allowedRolesSet.contains(loggedInUser.getRole()) || loggedInUser.getId().equals(userId);
                    if (isSelfOrAllowed) {
                        if (loggedInUser.getRole() == Role.GUARDIAN && !loggedInUser.getId().equals(userId)) {
                            return isGuardianOf(loggedInUser.getId(), userId);
                        }
                        return Mono.just(true);
                    } else {
                        return Mono.error(new AccessDenialException("Access denied"));
                    }
                });
    }

    public Mono<User> getLoggedInUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(User.class);
    }

    private Mono<Boolean> isGuardianOf(UUID guardianId, UUID studentId) {
        return studentGuardianRepository.existsByGuardianIdAndStudentId(guardianId, studentId);
    }
}
