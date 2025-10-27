package com.ecobazzar.ecobazzar.service;

import com.ecobazzar.ecobazzar.dto.LoginRequest;
import com.ecobazzar.ecobazzar.dto.RegisterRequest;
import com.ecobazzar.ecobazzar.dto.UserResponse;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.UserRepository;
import com.ecobazzar.ecobazzar.util.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register new user
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Default role = ROLE_USER
        String role = request.getRole() == null ? "ROLE_USER" : "ROLE_" + request.getRole().toUpperCase();

        if (role.equals("ROLE_ADMIN")) {
            throw new RuntimeException("Cannot self-register as admin!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEcoScore(0);

        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), 0, null);
    }

    // ✅ Login user
    public UserResponse login(LoginRequest login) {
        User user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEcoScore(), token);
    }
}
