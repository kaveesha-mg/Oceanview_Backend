package com.oceanview.controller;

import com.oceanview.dto.ReservationRequest;
import com.oceanview.dto.RoomRequest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.model.User;
import com.oceanview.service.FileStorageService;
import com.oceanview.service.ReservationService;
import com.oceanview.service.RoomService;
import com.oceanview.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RoomService roomService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public AdminController(RoomService roomService, ReservationService reservationService, UserService userService, FileStorageService fileStorageService) {
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload/room-image")
    public ResponseEntity<Map<String, String>> uploadRoomImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.store(file);
            return ResponseEntity.ok(Map.of("imageUrl", url != null ? url : ""));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> addRoom(@Valid @RequestBody RoomRequest req) {
        return ResponseEntity.ok(roomService.create(req));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable String id, @Valid @RequestBody RoomRequest req) {
        return ResponseEntity.ok(roomService.update(id, req));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable String id) {
        roomService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Room deleted"));
    }

    @PostMapping("/reservations/walk-in")
    public ResponseEntity<Reservation> createWalkInReservation(@Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservationService.create(req, null));
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable String id, @Valid @RequestBody ReservationRequest req) {
        return ResponseEntity.ok(reservationService.updateForAdmin(id, req));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        reservationService.deleteForAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
