package com.edziennikarze.gradebook.message.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class MessageFileUploadResponse {

    @NotNull
    private String fileId;

    @NotNull
    private String originalName;

    long fileSize;
}
