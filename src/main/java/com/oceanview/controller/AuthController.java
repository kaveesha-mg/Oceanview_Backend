package com.oceanview.controller;

import com.oceanview.dto.AuthResponse;
import com.oceanview.dto.LoginRequest;
import com.oceanview.dto.RegisterRequest;
import com.oceanview.model.User;
import com.oceanview.repository.UserRepository;
import com.oceanview.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        return userRepository.findByUsername(auth.getName())
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("userId", u.getId());
                    m.put("username", u.getUsername());
                    m.put("fullName", u.getFullName() != null ? u.getFullName() : "");
                    m.put("role", u.getRole().name());
                    return ResponseEntity.ok(m);
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
