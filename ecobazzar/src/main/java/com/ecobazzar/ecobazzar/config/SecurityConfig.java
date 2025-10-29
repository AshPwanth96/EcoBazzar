package com.ecobazzar.ecobazzar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import com.ecobazzar.ecobazzar.security.JwtFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // enables @PreAuthorize in controllers
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterConfig(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT stateless
            .authorizeHttpRequests(auth -> auth

                // Public auth endpoints
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                // Public product browsing (GET)
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                
             

                // Product management (create/update/delete) -> SELLER or ADMIN
                .requestMatchers("/api/products/**").hasAnyRole("SELLER", "ADMIN")

                // Cart / Checkout / Orders -> only USER role
                .requestMatchers("/api/cart/**", "/api/checkout/**", "/api/orders/**")
                    .hasRole("USER")

                // Admin endpoints -> ADMIN only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Others: authenticated
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Attach our JWT filter
            .formLogin(form -> form.disable()) // Disable form-based login
            .httpBasic(basic -> basic.disable()); // Disable browser popup login

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // For hashing passwords
    }
}
