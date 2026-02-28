package com.oceanview.repository;

import com.oceanview.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByRoomTypeAndAvailable(String roomType, boolean available);
    List<Room> findByAvailable(boolean available);
}
