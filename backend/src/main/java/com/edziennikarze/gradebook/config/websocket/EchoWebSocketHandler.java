package com.edziennikarze.gradebook.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EchoWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
            session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .map(this::parseTextMessage)
                    .map(text -> "Echo: " + text)
                    .map(session::textMessage));
    }

    private String parseTextMessage(String json) {
        try {
            Map<String, String> messageMap = objectMapper.readValue(json, Map.class);
            return messageMap.getOrDefault("text", json);
        } catch (IOException e) {
            return json;
        }
    }
}
