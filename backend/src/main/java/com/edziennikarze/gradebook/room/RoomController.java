package com.edziennikarze.gradebook.room;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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