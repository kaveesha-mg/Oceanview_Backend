package com.oceanview.controller;

import com.oceanview.dto.CreateReceptionistRequest;
import com.oceanview.model.User;
import com.oceanview.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/receptionist")
    public ResponseEntity<Map<String, Object>> createReceptionist(@Valid @RequestBody CreateReceptionistRequest req) {
        User user = userService.createReceptionist(req);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole().name()
        ));
    }

    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return userService.findAll().stream()
                .map(this::toSafeUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String id) {
        Optional<User> user = userService.findById(id);
        return user.map(u -> ResponseEntity.ok(toSafeUser(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toSafeUser(User u) {
        return Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "fullName", u.getFullName() != null ? u.getFullName() : "",
                "email", u.getEmail() != null ? u.getEmail() : "",
                "role", u.getRole().name()
        );
    }
}
