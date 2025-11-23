package com.edziennikarze.gradebook.message;

import com.edziennikarze.gradebook.message.dto.Message;
import com.edziennikarze.gradebook.message.dto.MessageFileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/history/{senderId}/{receiverId}")
    public Flux<Message> getMessageHistory(
            @PathVariable UUID senderId,
            @PathVariable UUID receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return messageService.getConversationHistory(senderId, receiverId, page, size);
    }

    @GetMapping("/file/{fileId:.+}")
    public Mono<ResponseEntity<Resource>> getMessageFile(@PathVariable String fileId) {
        return messageService.getMessageFile(fileId);
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<MessageFileUploadResponse> uploadFile(@RequestPart("file")Mono<FilePart> filePartMono) {
        return messageService.uploadFile(filePartMono);
    }
}
