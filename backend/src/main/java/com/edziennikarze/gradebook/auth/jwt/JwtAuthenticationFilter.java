package com.edziennikarze.gradebook.auth.jwt;

import com.edziennikarze.gradebook.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return extractTokenFromHeader(exchange)
                .flatMap(jwt -> processToken(jwt, exchange, chain))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<String> extractTokenFromHeader(ServerWebExchange exchange) {
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }
        return Mono.just(authHeader.substring(7));
    }

    private Mono<Void> processToken(String jwt, ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.justOrEmpty(extractUsername(jwt))
                .flatMap(userEmail -> authenticateUser(jwt, userEmail, exchange, chain))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Optional<String> extractUsername(String jwt) {
        try {
            return Optional.of(jwtUtil.extractUsername(jwt));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Mono<Void> authenticateUser(String jwt, String userEmail, ServerWebExchange exchange, WebFilterChain chain) {
        return userService.findByUsername(userEmail)
                .filter(userDetails -> jwtUtil.validateToken(jwt, userDetails))
                .flatMap(userDetails -> {
                    UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}

