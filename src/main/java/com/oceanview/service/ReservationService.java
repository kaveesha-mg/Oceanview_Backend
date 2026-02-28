package com.oceanview.service;

import com.oceanview.dto.ReservationRequest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.repository.ReservationRepository;
import com.oceanview.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public String generateReservationNumber() {
        String number;
        do {
            number = "OV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (reservationRepository.existsByReservationNumber(number));
        return number;
    }

    public Reservation create(ReservationRequest req, String customerUserId) {
        if (req.getCheckOutDate().isBefore(req.getCheckInDate()) || req.getCheckOutDate().equals(req.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Room room = findAvailableRoom(req.getRoomType(), req.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("No available room of type: " + req.getRoomType());
        }

        int nights = (int) ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        double totalBill = nights * room.getRatePerNight();

        Reservation r = new Reservation();
        r.setReservationNumber(generateReservationNumber());
        r.setGuestName(req.getGuestName());
        r.setAddress(req.getAddress());
        r.setNicNumber(req.getNicNumber());
        r.setContactNumber(req.getContactNumber());
        r.setRoomType(req.getRoomType());
        r.setRoomId(room.getId());
        r.setCheckInDate(req.getCheckInDate());
        r.setCheckInTime(req.getCheckInTime() != null ? req.getCheckInTime() : LocalTime.of(14, 0));
        r.setCheckOutDate(req.getCheckOutDate());
        r.setCheckOutTime(req.getCheckOutTime() != null ? req.getCheckOutTime() : LocalTime.of(11, 0));
        r.setNights(nights);
        r.setRatePerNight(room.getRatePerNight());
        r.setTotalBill(totalBill);
        r.setCustomerUserId(customerUserId);
        r.setStatus(Reservation.ReservationStatus.CONFIRMED);

        room.setAvailable(false);
        roomRepository.save(room);

        return reservationRepository.save(r);
    }

    private Room findAvailableRoom(String roomType, String roomId) {
        if (roomId != null && !roomId.isEmpty()) {
            return roomRepository.findById(roomId)
                    .filter(Room::isAvailable)
                    .orElse(null);
        }
        return roomRepository.findByRoomTypeAndAvailable(roomType, true).stream().findFirst().orElse(null);
    }

    public Optional<Reservation> findByReservationNumber(String number) {
        return reservationRepository.findByReservationNumber(number);
    }

    public Optional<Reservation> findById(String id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> findByCustomerUserId(String userId) {
        return reservationRepository.findByCustomerUserId(userId);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    private static boolean isOwner(String currentUserId, String reservationCustomerUserId) {
        if (currentUserId == null || currentUserId.isBlank()) return false;
        if (reservationCustomerUserId == null) return false;
        return currentUserId.trim().equals(reservationCustomerUserId.trim());
    }

    public Reservation update(String id, ReservationRequest req, String customerUserId) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (!isOwner(customerUserId, r.getCustomerUserId())) {
            throw new IllegalArgumentException("Not allowed to update this reservation");
        }
        if (req.getCheckOutDate().isBefore(req.getCheckInDate()) || req.getCheckOutDate().equals(req.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Room oldRoom = r.getRoomId() != null ? roomRepository.findById(r.getRoomId()).orElse(null) : null;
        if (oldRoom != null) {
            oldRoom.setAvailable(true);
            roomRepository.save(oldRoom);
        }

        Room room = findAvailableRoom(req.getRoomType(), req.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("No available room of type: " + req.getRoomType());
        }

        int nights = (int) ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        double totalBill = nights * room.getRatePerNight();

        r.setGuestName(req.getGuestName());
        r.setAddress(req.getAddress());
        r.setNicNumber(req.getNicNumber());
        r.setContactNumber(req.getContactNumber());
        r.setRoomType(req.getRoomType());
        r.setRoomId(room.getId());
        r.setCheckInDate(req.getCheckInDate());
        r.setCheckInTime(req.getCheckInTime() != null ? req.getCheckInTime() : LocalTime.of(14, 0));
        r.setCheckOutDate(req.getCheckOutDate());
        r.setCheckOutTime(req.getCheckOutTime() != null ? req.getCheckOutTime() : LocalTime.of(11, 0));
        r.setNights(nights);
        r.setRatePerNight(room.getRatePerNight());
        r.setTotalBill(totalBill);

        room.setAvailable(false);
        roomRepository.save(room);

        return reservationRepository.save(r);
    }

    public void delete(String id, String customerUserId) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (!isOwner(customerUserId, r.getCustomerUserId())) {
            throw new IllegalArgumentException("Not allowed to delete this reservation");
        }
        if (r.getRoomId() != null) {
            roomRepository.findById(r.getRoomId()).ifPresent(room -> {
                room.setAvailable(true);
                roomRepository.save(room);
            });
        }
        reservationRepository.delete(r);
    }

    /** Admin can update any reservation; bill is recalculated from room rate and nights. */
    public Reservation updateForAdmin(String id, ReservationRequest req) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (req.getCheckOutDate().isBefore(req.getCheckInDate()) || req.getCheckOutDate().equals(req.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Room oldRoom = r.getRoomId() != null ? roomRepository.findById(r.getRoomId()).orElse(null) : null;
        if (oldRoom != null) {
            oldRoom.setAvailable(true);
            roomRepository.save(oldRoom);
        }

        Room room = findAvailableRoom(req.getRoomType(), req.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("No available room of type: " + req.getRoomType());
        }

        int nights = (int) ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        double totalBill = nights * room.getRatePerNight();

        r.setGuestName(req.getGuestName());
        r.setAddress(req.getAddress());
        r.setNicNumber(req.getNicNumber());
        r.setContactNumber(req.getContactNumber());
        r.setRoomType(req.getRoomType());
        r.setRoomId(room.getId());
        r.setCheckInDate(req.getCheckInDate());
        r.setCheckInTime(req.getCheckInTime() != null ? req.getCheckInTime() : LocalTime.of(14, 0));
        r.setCheckOutDate(req.getCheckOutDate());
        r.setCheckOutTime(req.getCheckOutTime() != null ? req.getCheckOutTime() : LocalTime.of(11, 0));
        r.setNights(nights);
        r.setRatePerNight(room.getRatePerNight());
        r.setTotalBill(totalBill);

        room.setAvailable(false);
        roomRepository.save(room);

        return reservationRepository.save(r);
    }

    /** Admin can delete any reservation; room is marked available again. */
    public void deleteForAdmin(String id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (r.getRoomId() != null) {
            roomRepository.findById(r.getRoomId()).ifPresent(room -> {
                room.setAvailable(true);
                roomRepository.save(room);
            });
        }
        reservationRepository.delete(r);
    }
}
