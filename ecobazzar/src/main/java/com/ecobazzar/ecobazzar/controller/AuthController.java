package com.ecobazzar.ecobazzar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecobazzar.ecobazzar.dto.LoginRequest;
import com.ecobazzar.ecobazzar.dto.RegisterRequest;
import com.ecobazzar.ecobazzar.dto.UserResponse;
import com.ecobazzar.ecobazzar.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	//testing
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest register){
		return ResponseEntity.ok(authService.register(register));
	}
	
	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest login){
		return ResponseEntity.ok(authService.login(login));
	}

}
