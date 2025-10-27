package com.ecobazzar.ecobazzar.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecobazzar.ecobazzar.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // If no token, continue filter chain
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract claims (email + role)
        Claims claims = jwtUtil.getClaims(token);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        // Create Spring Authentication object
        var authority = new SimpleGrantedAuthority(role);
        var auth = new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

        // Save authentication in context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Continue request
        filterChain.doFilter(request, response);
    }
}
