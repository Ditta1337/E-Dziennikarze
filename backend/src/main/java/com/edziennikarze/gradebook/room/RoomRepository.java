package com.edziennikarze.gradebook.room;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends ReactiveCrudRepository<Room, UUID> {
}
