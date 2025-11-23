package com.edziennikarze.gradebook.message;

import com.edziennikarze.gradebook.message.dto.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, UUID> {

    @Query("SELECT * FROM messages WHERE " +
            "((sender_id = :user1Id AND receiver_id = :user2Id) OR " +
            "(sender_id = :user2Id AND receiver_id = :user1Id)) " +
            "ORDER BY created_at DESC " +
            "LIMIT :limit OFFSET :offset")
    Flux<Message> findConversationHistory(UUID user1Id, UUID user2Id, int limit, long offset);

    Mono<Message> findByFilePath(String filePath);
}
