package com.ecobazzar.ecobazzar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterConfig(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
    
//  @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//    	UserDetails admin = User.withUsername("admin")
//    			.password("{noop}admin123")
//    			.roles("ADMIN")
//    			.build();
//    	
//    	return new InMemoryUserDetailsManager(admin);
//    }
}
