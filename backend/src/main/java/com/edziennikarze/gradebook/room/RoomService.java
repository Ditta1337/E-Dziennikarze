package com.edziennikarze.gradebook.room;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.edziennikarze.gradebook.exception.ResourceNotFoundException;
import com.edziennikarze.gradebook.group.GroupService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final GroupService groupService;

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
        // TODO delete planned and modified lessons with this room assigned
        return roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Room with id " + roomId + " not found")))
                .flatMap(foundRoom -> {
                    return roomRepository.delete(foundRoom);
                });
    }
}
