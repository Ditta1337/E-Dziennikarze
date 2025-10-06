package com.edziennikarze.gradebook.config.websocket;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Component
public class WebSocketTokenAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getQueryParams().getFirst("token"))
                .map(token -> new UsernamePasswordAuthenticationToken(token, token));
    }
}
