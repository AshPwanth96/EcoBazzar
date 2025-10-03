package com.ecobazzar.ecobazzar.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecobazzar.ecobazzar.dto.LoginRequest;
import com.ecobazzar.ecobazzar.dto.RegisterRequest;
import com.ecobazzar.ecobazzar.dto.UserResponse;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.UserRepository;

@Service
public class AuthService {
	
	private final UserRepository userRepository;
	
	public AuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public UserResponse register(RegisterRequest request) {
		Optional<User> existing = userRepository.findByEmail(request.getEmail());
				if(existing.isPresent()) {
					throw new RuntimeException("Email is already taken");
				}
		
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setRole("CUSTOMER");
		user.setEcoScore(0);
		
		userRepository.save(user);
		
		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEcoScore());
		
	}
	
	public UserResponse login(LoginRequest login) {
		User user = userRepository.findByEmail(login.getEmail())
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		
		if(!user.getPassword().equals(login.getPassword())) {
			throw new RuntimeException("Invalid Password");
		}
		
		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getEcoScore());
				

	}

}
