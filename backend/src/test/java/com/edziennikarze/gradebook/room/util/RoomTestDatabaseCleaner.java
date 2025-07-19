package com.edziennikarze.gradebook.room.util;

import org.springframework.stereotype.Component;

import com.edziennikarze.gradebook.room.RoomRepository;

@Component
public class RoomTestDatabaseCleaner {

    private final RoomRepository roomRepository;

    public RoomTestDatabaseCleaner(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void cleanAll() {
        roomRepository.deleteAll().block();
    }
}
