package com.edziennikarze.gradebook.config.websocket;

import com.edziennikarze.gradebook.notification.websocket.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {
    private final EchoWebSocketHandler echoWebSocketHandler;
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, Object> map = new HashMap<>();
        map.put("/ws/echo", echoWebSocketHandler);
        map.put("/ws/notification", notificationWebSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}