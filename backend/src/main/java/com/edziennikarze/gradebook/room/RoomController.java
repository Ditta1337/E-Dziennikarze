package com.edziennikarze.gradebook.room;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public Mono<Room> createRoom(@RequestBody Mono<Room> roomMono) {
        return roomService.createRoom(roomMono);
    }

    @GetMapping("/all")
    public Flux<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{roomId}")
    public Mono<Room> getRoomById(@PathVariable UUID roomId) {
        return roomService.getRoomById(roomId);
    }

    @PutMapping
    public Mono<Room> updateRoom(@RequestBody Mono<Room> roomMono) {
        return roomService.updateRoom(roomMono);
    }

    @DeleteMapping("/{roomId}")
    public Mono<Void> deleteRoom(@PathVariable UUID roomId) {
        return roomService.deleteRoom(roomId);
    }
}
