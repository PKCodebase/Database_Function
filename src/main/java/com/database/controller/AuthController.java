package com.database.controller;




import com.database.dto.LoginRequest;
import com.database.dto.RefreshTokenRequest;
import com.database.dto.RegisterRequest;
import com.database.response.AuthResponse;
import com.database.service.impl.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Request received → POST /register | email: {}", request.getEmail());
        String result = authService.register(request);
        log.info("User registered → {}", result);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Request received → POST /login | email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.info("Login successful → email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        log.info("Request received → POST /refresh");
        AuthResponse response = authService.refreshAccessToken(request);
        log.info("Access token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        log.info("Request received → POST /logout");
        authService.logout(request.getRefreshToken());
        log.info("Logout successful");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}