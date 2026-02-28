package com.oceanview.controller;

import com.oceanview.dto.ReservationRequest;
import com.oceanview.model.Reservation;
import com.oceanview.repository.UserRepository;
import com.oceanview.security.JwtAuthDetails;
import com.oceanview.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    private String resolveCurrentUserId(Authentication auth) {
        if (auth == null || auth.getName() == null) return null;
        if (auth.getDetails() instanceof JwtAuthDetails) {
            String id = ((JwtAuthDetails) auth.getDetails()).getUserId();
            if (id != null && !id.isBlank()) return id;
        }
        var user = userRepository.findByUsername(auth.getName());
        if (user.isEmpty())
            user = userRepository.findByUsernameRegex("^" + Pattern.quote(auth.getName()) + "$");
        return user.map(u -> u.getId()).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody ReservationRequest req, Authentication auth) {
        return ResponseEntity.ok(reservationService.create(req, resolveCurrentUserId(auth)));
    }

    @GetMapping("/my")
    public List<Reservation> myReservations(Authentication auth) {
        String userId = resolveCurrentUserId(auth);
        if (userId == null) return List.of();
        return reservationService.findByCustomerUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable String id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<Reservation> getByNumber(@PathVariable String number) {
        return reservationService.findByReservationNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(@PathVariable String id, @Valid @RequestBody ReservationRequest req, Authentication auth) {
        return ResponseEntity.ok(reservationService.update(id, req, resolveCurrentUserId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication auth) {
        reservationService.delete(id, resolveCurrentUserId(auth));
        return ResponseEntity.noContent().build();
    }
}
