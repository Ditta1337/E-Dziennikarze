package com.edziennikarze.gradebook.auth.api;

import com.edziennikarze.gradebook.exception.AccessDenialException;
import com.edziennikarze.gradebook.user.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Component("apiKeyAuthenticationManager")
public class ApiKeyAuthenticationManager implements ReactiveAuthenticationManager {

    @Value("${solver.api.key}")
    private String configuredApiKey;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String apiKey = authentication.getPrincipal().toString();

        if (configuredApiKey.equals(apiKey)) {
            return Mono.just(new UsernamePasswordAuthenticationToken(
                    "solver",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(Role.ADMIN.name()))
            ));
        } else {
            return Mono.error(new AccessDenialException("Invalid API Key for solver access"));
        }
    }
}
