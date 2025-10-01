package com.edziennikarze.gradebook.auth;

import com.edziennikarze.gradebook.auth.jwt.JwtUtil;
import com.edziennikarze.gradebook.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Primary
public class AuthManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String username;

        try {
            username = jwtUtil.extractUsername(authToken);
        } catch (Exception e) {
            return Mono.empty();
        }

        if (username == null) {
            return Mono.empty();
        }

        return userService.findByUsername(username)
                .flatMap(userDetails -> {
                    if (Boolean.TRUE.equals(jwtUtil.validateToken(authToken, userDetails))) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        return Mono.just(auth);
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
