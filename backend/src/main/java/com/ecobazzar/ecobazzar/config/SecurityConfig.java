package com.ecobazzar.ecobazzar.config;

import com.ecobazzar.ecobazzar.security.JwtFilter;

import jakarta.servlet.http.HttpServletResponse;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
          // Auth endpoints
          .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

          // Swagger
          .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

          // Public product browsing
          .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()

          // Reports (USER only)
          .requestMatchers("/api/reports/user/**").hasRole("USER")

          // Seller endpoints
          .requestMatchers(HttpMethod.GET, "/api/products/seller").hasAnyRole("SELLER", "ADMIN")
          .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("SELLER", "ADMIN")
          .requestMatchers(HttpMethod.PUT,  "/api/products/**").hasAnyRole("SELLER", "ADMIN")
          .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasAnyRole("SELLER", "ADMIN")

          // Admin endpoints
          .requestMatchers("/api/admin/**").hasRole("ADMIN")
          .requestMatchers("/api/admin-request/pending").hasRole("ADMIN")
          .requestMatchers("/api/admin-request/approve/**").hasRole("ADMIN")
          .requestMatchers("/api/admin-request/reject/**").hasRole("ADMIN")

          // Eco-certification requests (any logged-in user)
          .requestMatchers("/api/admin-request/request").authenticated()
          .requestMatchers("/api/admin-request/has-pending").authenticated()

          // Everything else must be authenticated
          .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
          // 401 for missing/invalid token
          .authenticationEntryPoint((req, res, e) ->
              res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
          // 403 for logged-in but insufficient role
          .accessDeniedHandler((req, res, e) ->
              res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

      return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}