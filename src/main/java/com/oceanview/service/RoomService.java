package com.oceanview.service;

import com.oceanview.dto.RoomRequest;
import com.oceanview.model.Room;
import com.oceanview.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAvailable() {
        return roomRepository.findByAvailable(true);
    }

    public List<Room> findAvailableByType(String roomType) {
        return roomRepository.findByRoomTypeAndAvailable(roomType, true);
    }

    public Optional<Room> findById(String id) {
        return roomRepository.findById(id);
    }

    public Room create(RoomRequest req) {
        if (roomRepository.findByRoomNumber(req.getRoomNumber()).isPresent()) {
            throw new IllegalArgumentException("Room number already exists");
        }
        Room room = new Room();
        room.setRoomNumber(req.getRoomNumber());
        room.setRoomType(req.getRoomType());
        room.setRatePerNight(req.getRatePerNight());
        room.setDescription(req.getDescription());
        room.setImageUrl(req.getImageUrl());
        room.setAvailable(true);
        return roomRepository.save(room);
    }

    public Room update(String id, RoomRequest req) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setRoomNumber(req.getRoomNumber());
        room.setRoomType(req.getRoomType());
        room.setRatePerNight(req.getRatePerNight());
        room.setDescription(req.getDescription());
        room.setImageUrl(req.getImageUrl());
        return roomRepository.save(room);
    }

    public void delete(String id) {
        roomRepository.deleteById(id);
    }
}
