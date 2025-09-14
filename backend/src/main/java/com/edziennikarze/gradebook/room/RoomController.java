package com.edziennikarze.gradebook.room;

import static com.edziennikarze.gradebook.user.Role.*;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edziennikarze.gradebook.auth.annotation.AuthorizationAnnotation.*;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<Room> createRoom(@RequestBody Mono<Room> roomMono) {
        return roomService.createRoom(roomMono);
    }

    @GetMapping("/all")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Flux<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{roomId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER, PRINCIPAL, GUARDIAN, STUDENT, TEACHER})
    public Mono<Room> getRoomById(@PathVariable UUID roomId) {
        return roomService.getRoomById(roomId);
    }

    @PutMapping
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<Room> updateRoom(@RequestBody Mono<Room> roomMono) {
        return roomService.updateRoom(roomMono);
    }

    @DeleteMapping("/{roomId}")
    @HasAnyRole({ADMIN, OFFICE_WORKER })
    public Mono<Void> deleteRoom(@PathVariable UUID roomId) {
        return roomService.deleteRoom(roomId);
    }
}
