package com.edziennikarze.gradebook.message;

import com.edziennikarze.gradebook.auth.util.LoggedInUserService;
import com.edziennikarze.gradebook.exception.AccessDenialException;
import com.edziennikarze.gradebook.message.dto.Message;
import com.edziennikarze.gradebook.message.dto.MessageFileUploadResponse;
import com.edziennikarze.gradebook.notification.NotificationService;
import com.edziennikarze.gradebook.user.UserRepository;
import com.edziennikarze.gradebook.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final LoggedInUserService loggedInUserService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Value("${file.storage.path:./uploads/messages/}")
    private String storagePath;

    public Flux<Message> getConversationHistory(UUID senderId, UUID receiverId, int page, int size) {
        return loggedInUserService.getLoggedInUser()
                .filter(loggedInUser -> userIsPartOfConversation(loggedInUser, senderId, receiverId))
                .switchIfEmpty(Mono.error(new AccessDenialException("Access denied")))
                .flatMapMany(loggedInUser -> {
                    long offset = (long) page * size;
                    return messageRepository.findConversationHistory(senderId, receiverId, size, offset);
                });
    }

    public Mono<ResponseEntity<Resource>> getMessageFile(String fileId) {
        return sanitizeFileId(fileId)
                .flatMap(this::getAuthorizedMessage)
                .flatMap(this::buildFileResponse);
    }

    private Mono<String> sanitizeFileId(String fileId) {
        if (fileId.contains("..") || fileId.contains("/") || fileId.contains("\\")) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid File ID"));
        }
        return Mono.just(fileId);
    }

    private Mono<Message> getAuthorizedMessage(String fileId) {
        return messageRepository.findByFilePath(fileId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "File (message) not found")))
                .flatMap(this::checkAuthorization);
    }

    private Mono<Message> checkAuthorization(Message message) {
        return loggedInUserService.getLoggedInUser()
                .flatMap(loggedInUser -> {
                    if (!loggedInUser.getId().equals(message.getSenderId()) &&
                            !loggedInUser.getId().equals(message.getReceiverId())) {

                        log.warn("User {} attempted to access unauthorized file {}", loggedInUser.getId(), message.getFilePath());
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied"));
                    }
                    return Mono.just(message);
                });
    }

    private Mono<ResponseEntity<Resource>> buildFileResponse(Message message) {
        Path filePath = Paths.get(storagePath).resolve(message.getFilePath()).normalize();
        Resource resource = new FileSystemResource(filePath);

        return Mono.just(resource)
                .filter(Resource::exists)
                .flatMap(res ->
                        Mono.fromCallable(() -> Files.probeContentType(filePath))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(mimeType -> ResponseEntity.ok()
                                        .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : "application/octet-stream"))
                                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + message.getContent() + "\"")
                                        .body(res)
                                )
                                .onErrorResume(IOException.class, e -> {
                                    log.error("Could not determine file type for {}: {}", message.getFilePath(), e.getMessage());
                                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not determine file type"));
                                })
                )
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "File resource not found")));
    }


    public Mono<MessageFileUploadResponse> uploadFile(Mono<FilePart> filePartMono) {
        Path directory = Paths.get(storagePath);
        if (Files.notExists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create storage directory"));
            }
        }

        return filePartMono.flatMap(filePart -> {
            String originalName = filePart.filename();
            String extension = "";
            int i = originalName.lastIndexOf('.');
            if (i > 0) {
                extension = originalName.substring(i);
            }

            String uniqueName = UUID.randomUUID() + extension;
            Path destination = directory.resolve(uniqueName);

            return filePart.transferTo(destination)
                    .then(Mono.zip(Mono.just(filePart.headers().getContentLength()), Mono.just(uniqueName)))
                    .map(tuple -> buildFileUploadResponse(tuple.getT2(), originalName, tuple.getT1()));
        });
    }

    private MessageFileUploadResponse buildFileUploadResponse(String storedFileName, String originalName, long fileSize) {
        return MessageFileUploadResponse.builder()
                .fileId(storedFileName)
                .originalName(originalName)
                .fileSize(fileSize)
                .build();
    }

    public Mono<Message> saveNewMessage(Message messagePayload, UUID senderId) {
        Message safeMessage = Message.builder()
                .senderId(senderId)
                .receiverId(messagePayload.getReceiverId())
                .content(messagePayload.getContent())
                .type(messagePayload.getType())
                .filePath(messagePayload.getFilePath())
                .status(MessageStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(safeMessage)
                .flatMap(savedMessage ->
                        userRepository.findById(senderId)
                                .map(sender -> String.format("Otrzymałeś nową wiadomość od %s %s (%s)",
                                        sender.getName(), sender.getSurname(), sender.getEmail()))
                                .defaultIfEmpty("Otrzymałeś nową wiadomość")
                                .flatMap(messageText ->
                                        notificationService.sendNotification(savedMessage.getReceiverId(), messageText)
                                )
                                .thenReturn(savedMessage)
                );
    }

    public Mono<Message> editMessage(Message messagePayload, UUID senderId) {
        return messageRepository.findById(messagePayload.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")))
                .flatMap(existingMessage -> {
                    if (!existingMessage.getSenderId().equals(senderId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot edit this message"));
                    }
                    if (existingMessage.getStatus() == MessageStatus.DELETED) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot edit a deleted message"));
                    }

                    existingMessage.setContent(messagePayload.getContent());
                    existingMessage.setStatus(MessageStatus.EDITED);
                    existingMessage.setUpdatedAt(LocalDateTime.now());

                    return messageRepository.save(existingMessage);
                });
    }

    public Mono<Message> deleteMessage(UUID messageId, UUID senderId) {
        return messageRepository.findById(messageId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")))
                .flatMap(existingMessage -> {
                    if (!existingMessage.getSenderId().equals(senderId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot delete this message"));
                    }

                    existingMessage.setStatus(MessageStatus.DELETED);
                    existingMessage.setUpdatedAt(LocalDateTime.now());

                    return messageRepository.save(existingMessage);
                });
    }


    private boolean userIsPartOfConversation(User user, UUID senderId, UUID receiverId) {
        return user.getId().equals(senderId) || user.getId().equals(receiverId);
    }
}