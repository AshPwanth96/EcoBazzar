package com.ecobazzar.ecobazzar.service;

import org.springframework.stereotype.Service;

import com.ecobazzar.ecobazzar.dto.UserReport;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.OrderRepository;
import com.ecobazzar.ecobazzar.repository.UserRepository;

@Service
public class UserReportService {
	
	private final UserRepository userRepository;
	
	private final OrderRepository orderRepository;
	
	public UserReportService(UserRepository userRepository, OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
	}
	
	public UserReport getUserReport(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(()-> new RuntimeException("User not found"));
		
		Long totalPurchase = (long)orderRepository.findByUserId(userId).size();
		
		Double totalSpent = orderRepository.getTotalSpendByUser(userId);
		
		Double totalCarbon = orderRepository.getTotalCarbonByUser(userId);
		
		if(totalCarbon == null) {
			totalCarbon = 0.0;
		}
		
		if(totalSpent == null) {
			totalSpent = 0.0;
		}
		
		String badge = getEcoBadge(totalCarbon);
		
		return new UserReport(
				user.getId(),
				user.getName(),
				totalPurchase,
				totalSpent,
				totalCarbon,
				badge);
	}
	
	private String getEcoBadge(Double totalCarbon) {
		if(totalCarbon>500) { 
			return "🌎 Eco Legend";
		}else if(totalCarbon>200){
			return "🌿 Green Hero";
			
		}else if(totalCarbon>100) {
			return "🍃 Conscious Shopper";
		}else if(totalCarbon>0) {
			return "🌱 Beginner Eco-Saver";
		}
			else{
				return "🚫 No Impact Yet";
			}
		
	}

}
