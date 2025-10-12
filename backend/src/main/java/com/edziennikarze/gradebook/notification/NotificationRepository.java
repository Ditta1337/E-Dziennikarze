package com.edziennikarze.gradebook.notification;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, UUID> {

    Flux<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);

    @Modifying
    @Query("UPDATE notifications SET read = true WHERE user_id = :userId AND read = false")
    Mono<Void> markAllAsReadForUser(UUID userId);
}
