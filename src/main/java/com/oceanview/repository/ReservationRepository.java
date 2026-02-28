package com.oceanview.repository;

import com.oceanview.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    List<Reservation> findByCustomerUserId(String customerUserId);
    boolean existsByReservationNumber(String reservationNumber);
}
