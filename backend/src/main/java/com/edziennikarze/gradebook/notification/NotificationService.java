package com.edziennikarze.gradebook.notification;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.AccessDenialException;
import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDispatcher dispatcher;
    private final NotificationRepository notificationRepository;
    private final LoggedInUserService loggedInUserService;

    public Mono<Void> sendNotification(UUID userId, String message) {
        return createNotification(userId, message)
                .flatMap(notificationRepository::save)
                .doOnSuccess(notification -> dispatcher.dispatch(notification.getUserId(), notification))
                .then();
    }

    public Flux<Notification> getUnreadNotifications(UUID userId) {
        return loggedInUserService.getLoggedInUser()
                .filter(currentUser -> currentUser.getId().equals(userId))
                .switchIfEmpty(Mono.error(new AccessDenialException("Cannot access notifications of other users")))
                .flatMapMany(currentUser -> notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId));
    }

    @Transactional
    public Mono<Void> markAsRead(UUID notificationId) {
        return loggedInUserService.getLoggedInUser()
                .flatMap(currentUser -> notificationRepository.findById(notificationId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Notification not found")))
                        .filter(notification -> notification.getUserId().equals(currentUser.getId()))
                        .switchIfEmpty(Mono.error(new AccessDenialException("Cannot mark this notification as read")))
                        .flatMap(notification -> {
                            notification.setRead(true);
                            return notificationRepository.save(notification);
                        })
                )
                .then();
    }

    @Transactional
    public Mono<Void> markAllAsRead() {
        return loggedInUserService.getLoggedInUser()
                .flatMap(user -> notificationRepository.markAllAsReadForUser(user.getId()));
    }

    private Mono<Notification> createNotification(UUID userId, String message) {
        return Mono.just(Notification.builder()
                .userId(userId)
                .message(message)
                .build());
    }
}