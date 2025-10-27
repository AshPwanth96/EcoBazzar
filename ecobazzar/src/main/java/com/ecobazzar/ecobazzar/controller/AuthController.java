package com.ecobazzar.ecobazzar.controller;

import com.ecobazzar.ecobazzar.dto.LoginRequest;
import com.ecobazzar.ecobazzar.dto.RegisterRequest;
import com.ecobazzar.ecobazzar.dto.UserResponse;
import com.ecobazzar.ecobazzar.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
