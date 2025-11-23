package com.edziennikarze.gradebook.message.dto;

import com.edziennikarze.gradebook.message.MessageStatus;
import com.edziennikarze.gradebook.message.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("messages")
public class Message {

    @Id
    private UUID id;

    @NotNull
    private UUID senderId;

    @NotNull
    private UUID receiverId;

    private String content;

    @NotNull
    private MessageType type;

    @NotNull
    private MessageStatus status;

    private String filePath;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
