package com.edziennikarze.gradebook.message.websocket;

import com.edziennikarze.gradebook.message.MessageService;
import com.edziennikarze.gradebook.message.dto.Message;
import com.edziennikarze.gradebook.message.dto.MessageCommand;
import com.edziennikarze.gradebook.user.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageWebSocketHandler implements WebSocketHandler {

    private final MessageService messageService;

    private final ObjectMapper objectMapper;
    private final Map<UUID, Sinks.Many<String>> userSinks = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Mono<UUID> userIdMono = session.getHandshakeInfo().getPrincipal()
                .handle((principal, sink) -> {
                    if (principal instanceof Authentication) {
                        Object principalObj = ((Authentication) principal).getPrincipal();
                        if (principalObj instanceof User) {
                            User user = (User) principalObj;
                            sink.next(user.getId());
                            return;
                        }
                    }
                    sink.error(new IllegalStateException("Unexpected principal type: " + (principal != null ? principal.getClass() : "null")));
                });

        return userIdMono.flatMap(userId -> {
            log.info("Message WebSocket user connected: {}", userId);

            Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
            userSinks.put(userId, sink);

            Mono<Void> output = session.send(
                    sink.asFlux().map(session::textMessage)
            );

            Mono<Void> input = session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .flatMap(json -> processIncomingMessage(json, userId))
                    .doOnError(e -> log.error("Error processing message for user {}: {}", userId, e.getMessage()))
                    .then();

            return Mono.zip(input, output)
                    .then()
                    .doFinally(signalType -> {
                        log.info("Message WebSocket user disconnected: {}", userId);
                        userSinks.remove(userId);
                    });
        });
    }

    private Mono<Void> processIncomingMessage(String jsonPayload, UUID senderId) {
        try {
            MessageCommand command = objectMapper.readValue(jsonPayload, MessageCommand.class);
            Message message = command.getMessage();
            Mono<Message> messageMono;

            switch (command.getAction()) {
                case NEW -> messageMono = messageService.saveNewMessage(message, senderId);
                case EDIT -> messageMono = messageService.editMessage(message, senderId);
                case DELETE -> messageMono = messageService.deleteMessage(message.getId(), senderId);
                default -> {
                    log.error("Unknown message action: {}", command.getAction());
                    return Mono.empty();
                }
            }

            return messageMono
                    .doOnSuccess(this::dispatchMessage)
                    .doOnError(e -> log.error("Failed to process message: {}", e.getMessage()))
                    .then();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse WebSocket message: {}", e.getMessage());
            return Mono.error(e);
        }
    }

    private void dispatchMessage(Message message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);

            UUID senderId = message.getSenderId();
            UUID receiverId = message.getReceiverId();

            Sinks.Many<String> receiverSink = userSinks.get(receiverId);
            if (receiverSink != null) {
                log.info("Dispatching message {} to receiver {}", message.getId(), receiverId);
                receiverSink.tryEmitNext(messageJson);
            } else {
                log.info("Receiver {} not online. Message {} will be in history.", receiverId, message.getId());
            }

            Sinks.Many<String> senderSink = userSinks.get(senderId);
            if (senderSink != null) {
                log.info("Dispatching message {} to sender {}", message.getId(), senderId);
                senderSink.tryEmitNext(messageJson);
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for dispatch: {}", e.getMessage());
        }
    }
}