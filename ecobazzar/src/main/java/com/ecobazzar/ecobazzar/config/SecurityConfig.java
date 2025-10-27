package com.ecobazzar.ecobazzar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecobazzar.ecobazzar.security.JwtFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterConfig(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login"
                ).permitAll() // ✅ Public endpoints
                .anyRequest().authenticated() // Everything else requires JWT
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Attach our JWT filter
            .formLogin(form -> form.disable()) // Disable form-based login
            .httpBasic(basic -> basic.disable()); // Disable browser popup login

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // ✅ For hashing passwords
    }
}
