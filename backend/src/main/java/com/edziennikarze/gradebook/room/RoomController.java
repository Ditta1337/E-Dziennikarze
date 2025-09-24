package com.edziennikarze.gradebook.room;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Room> createRoom(@RequestBody Mono<Room> roomMono) {
        return roomService.createRoom(roomMono);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Flux<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{roomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER', 'PRINCIPAL', 'GUARDIAN', 'STUDENT', 'TEACHER')")
    public Mono<Room> getRoomById(@PathVariable UUID roomId) {
        return roomService.getRoomById(roomId);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Room> updateRoom(@RequestBody Mono<Room> roomMono) {
        return roomService.updateRoom(roomMono);
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OFFICE_WORKER')")
    public Mono<Void> deleteRoom(@PathVariable UUID roomId) {
        return roomService.deleteRoom(roomId);
    }
}