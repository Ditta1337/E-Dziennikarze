package com.edziennikarze.gradebook.room;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Mono<Room> createRoom(Mono<Room> roomMono) {
        return roomMono.flatMap(roomRepository::save);
    }

    public Flux<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Mono<Room> getRoomById(UUID roomId) {
        return roomRepository.findById(roomId);
    }

    public Mono<Room> updateRoom(Mono<Room> roomMono) {
        return roomMono.flatMap(room -> roomRepository.findById(room.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Room with id " + room.getId() + " not found")))
                .flatMap(existingRoom -> {
                    existingRoom.setRoomCode(room.getRoomCode());
                    existingRoom.setCapacity(room.getCapacity());
                    return roomRepository.save(existingRoom);
                }));
    }

    public Mono<Void> deleteRoom(UUID roomId) {
        return roomRepository.deleteById(roomId);
    }
}
