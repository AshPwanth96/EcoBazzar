package com.ecobazzar.ecobazzar.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.UserRepository;
import com.ecobazzar.ecobazzar.service.UserReportService;
import com.ecobazzar.ecobazzar.dto.UserReport;

@RestController
@RequestMapping("/api/reports")
public class UserReportController {

    private final UserReportService userReportService;
    private final UserRepository userRepository;

    public UserReportController(UserReportService userReportService, UserRepository userRepository) {
        this.userReportService = userReportService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public UserReport getUserReport() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Call the report service using the authenticated user's ID
        return userReportService.getUserReport(currentUser.getId());
    }
}
