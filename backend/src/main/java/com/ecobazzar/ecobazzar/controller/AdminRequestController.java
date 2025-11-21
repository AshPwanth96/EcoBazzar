package com.ecobazzar.ecobazzar.controller;

import com.ecobazzar.ecobazzar.dto.PendingAdminRequestDto;
import com.ecobazzar.ecobazzar.model.AdminRequest;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.UserRepository;
import com.ecobazzar.ecobazzar.service.AdminRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin-request")
public class AdminRequestController {

    private final AdminRequestService service;
    private final UserRepository userRepository;

    public AdminRequestController(AdminRequestService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    // Any logged-in user can request to become admin
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestAccess(Authentication auth) {
        String email = auth.getName(); // JWT subject = email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        service.requestAdminAccess(user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin access requested successfully");
        return ResponseEntity.ok(response);
    }

    // Only admins can see pending requests
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public List<PendingAdminRequestDto> getPending() {
        return service.getPendingRequests().stream()
            .map(req -> new PendingAdminRequestDto(
                req.getId(),
                req.getUser().getId(),
                req.getUser().getName(),
                req.getUser().getEmail(),
                req.getRequestedAt()
            ))
            .toList();
    }
    // Approve → make user admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<Map<String, String>> approve(@PathVariable Long id) {
        service.approveRequest(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User promoted to Admin successfully");
        return ResponseEntity.ok(response);
    }

    // Reject request
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reject/{id}")
    public ResponseEntity<Map<String, String>> reject(@PathVariable Long id) {
        service.rejectRequest(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin request rejected");
        return ResponseEntity.ok(response);
    }

    // Badge indicator — any logged-in user can call this
    @GetMapping("/has-pending")
    public boolean hasPending() {
        return service.hasPendingRequests();
    }
}