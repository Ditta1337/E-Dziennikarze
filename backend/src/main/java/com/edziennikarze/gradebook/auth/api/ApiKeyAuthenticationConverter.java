package com.edziennikarze.gradebook.auth.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class ApiKeyAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER))
                .map(apiKey -> new UsernamePasswordAuthenticationToken(apiKey, apiKey));
    }
}
