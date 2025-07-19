package com.edziennikarze.gradebook.room;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RoomRepository extends ReactiveCrudRepository<Room, UUID> {
}
