package com.edziennikarze.gradebook.notification.websocket;

import com.edziennikarze.gradebook.notification.NotificationDispatcher;
import com.edziennikarze.gradebook.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler implements WebSocketHandler {

    private final NotificationDispatcher dispatcher;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Mono<UUID> userIdMono = session.getHandshakeInfo().getPrincipal()
                .handle((principal, sink) -> {
                    if (principal instanceof Authentication) {
                        User user = (User) ((Authentication) principal).getPrincipal();
                        sink.next(user.getId());
                        return;
                    }
                    sink.error(new IllegalStateException("Unexpected principal type: " + principal.getClass()));
                });

        return userIdMono.flatMap(userId -> {
            dispatcher.register(userId);
            Sinks.Many<String> sink = dispatcher.getSink(userId);

            Mono<Void> output = session.send(
                    sink.asFlux().map(session::textMessage)
            );

            Mono<Void> input = session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(message -> log.info("Received from {}: {}", userId, message))
                    .then();

            return Mono.zip(input, output)
                    .then()
                    .doFinally(signalType -> dispatcher.unregister(userId));
        });
    }
}