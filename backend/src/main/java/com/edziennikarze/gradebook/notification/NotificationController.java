package com.edziennikarze.gradebook.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread/user/{userId}")
    public Flux<Notification> getUnreadNotifications(@PathVariable UUID userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    @PatchMapping("/{notificationId}/read")
    public Mono<Void> markAsRead(@PathVariable UUID notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    @PatchMapping("/read-all")
    public Mono<Void> markAllAsRead() {
        return notificationService.markAllAsRead();
    }
}
