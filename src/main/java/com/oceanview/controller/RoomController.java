package com.oceanview.controller;

import com.oceanview.model.Room;
import com.oceanview.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.findAll();
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms(@RequestParam(required = false) String roomType) {
        if (roomType != null && !roomType.isEmpty()) {
            return roomService.findAvailableByType(roomType);
        }
        return roomService.findAvailable();
    }
}
