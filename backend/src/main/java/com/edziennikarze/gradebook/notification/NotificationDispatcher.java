package com.edziennikarze.gradebook.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final ObjectMapper objectMapper;
    private final Map<UUID, Sinks.Many<String>> userSinks = new ConcurrentHashMap<>();

    public void register(UUID userId) {
        userSinks.put(userId, Sinks.many().multicast().onBackpressureBuffer());
        log.info("Registered WebSocket session for user: {}", userId);
    }

    public void unregister(UUID userId) {
        userSinks.remove(userId);
        log.info("Unregistered WebSocket session for user: {}", userId);
    }

    public void dispatch(UUID userId, Object notificationPayload) {
        Sinks.Many<String> sink = userSinks.get(userId);
        if (sink == null) {
            log.warn("No active WebSocket session for user: {}", userId);
            return;
        }
        try {
            String message = objectMapper.writeValueAsString(notificationPayload);
            sink.tryEmitNext(message);
        } catch (Exception e) {
            log.error("Failed to dispatch notification for user: {}", userId, e);
        }
    }

    public Sinks.Many<String> getSink(UUID userId) {
        return userSinks.get(userId);
    }
}
