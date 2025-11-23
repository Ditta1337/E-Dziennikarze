package com.edziennikarze.gradebook.message.dto;

import com.edziennikarze.gradebook.message.MessageAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageCommand {

    @NotNull
    private Message message;

    @NotNull
    private MessageAction action;
}
