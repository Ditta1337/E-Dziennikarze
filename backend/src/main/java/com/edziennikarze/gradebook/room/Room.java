package com.edziennikarze.gradebook.room;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("rooms")
public class Room {

    @Id
    private UUID id;

    @NotNull
    private String roomCode;

    private int capacity;
}
