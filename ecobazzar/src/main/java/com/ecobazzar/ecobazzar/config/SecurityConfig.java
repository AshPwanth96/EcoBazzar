package com.ecobazzar.ecobazzar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecobazzar.ecobazzar.security.JwtFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterConfig(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // public endpoints
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // public product browsing
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/products/**").permitAll()

                // product management (SELLER or ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/products/**").hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**", "/products/**").hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/products/**").hasAnyAuthority("ROLE_SELLER", "ROLE_ADMIN")

                // cart / checkout / orders (USER only)
                .requestMatchers("/api/cart/**", "/api/checkout/**", "/api/orders/**").hasAuthority("ROLE_USER")

                // admin endpoints
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // anything else
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
