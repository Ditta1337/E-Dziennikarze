package com.edziennikarze.gradebook.room;

import static com.edziennikarze.gradebook.utils.TestObjectBuilder.buildRoom;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;

import com.edziennikarze.gradebook.config.PostgresTestContainerConfig;
import com.edziennikarze.gradebook.room.util.RoomTestDatabaseCleaner;

import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.port=0")
@ImportTestcontainers(PostgresTestContainerConfig.class)
class RoomControllerIntTest {

    @Autowired
    private RoomController roomController;

    @Autowired
    private RoomTestDatabaseCleaner roomTestDatabaseCleaner;

    @Autowired
    private RoomRepository roomRepository;

    private List<Room> rooms;

    @BeforeEach
    void setUp() {
        setUpRooms();
    }

    @AfterEach
    void tearDown() {
        roomTestDatabaseCleaner.cleanAll();
    }

    @Test
    void shouldCreateRooms() {
        // when
        List<Room> createdRooms = rooms.stream()
                .map(room -> roomController.createRoom(Mono.just(room))
                        .block())
                .toList();

        // then
        assertEquals(rooms.size(), createdRooms.size());
        createdRooms.forEach(room -> assertNotNull(room.getId()));
    }

    @Test
    void shouldGetAllRooms() {
        // given
        List<Room> savedRooms = roomRepository.saveAll(rooms)
                .collectList()
                .block();

        // when
        List<Room> allRooms = roomController.getAllRooms()
                .collectList()
                .block();

        // then
        assertEquals(savedRooms, allRooms);
    }

    @Test
    void shouldUpdateRoom() {
        // given
        List<Room> savedRooms = roomRepository.saveAll(rooms)
                .collectList()
                .block();

        Room originalRoom = savedRooms.getFirst();
        Room updatedOriginalRoom = buildRoom(35, "updated_1");
        updatedOriginalRoom.setId(originalRoom.getId());

        // when
        Room savedUpdatedRoom = roomController.updateRoom(Mono.just(updatedOriginalRoom))
                .block();

        // then
        assertEquals(updatedOriginalRoom, savedUpdatedRoom);
        assertNotEquals(originalRoom, savedUpdatedRoom);
    }

    @Test
    void shouldDeleteRoom() {
        // given
        List<Room> savedRooms = roomRepository.saveAll(rooms)
                .collectList()
                .block();

        // when
        roomController.deleteRoom(savedRooms.getFirst()
                        .getId())
                .block();
        List<Room> allRoomsAfterDelete = roomRepository.findAll()
                .collectList()
                .block();

        // then
        assertEquals(savedRooms.size() - 1, allRoomsAfterDelete.size());
    }

    private void setUpRooms() {
        rooms = List.of(buildRoom(30, "1"), buildRoom(20, "2"), buildRoom(20, "3"));
    }
}
