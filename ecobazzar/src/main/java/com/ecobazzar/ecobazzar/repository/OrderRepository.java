package com.ecobazzar.ecobazzar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecobazzar.ecobazzar.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByUserId(Long userId);
	
	@Query("SELECT SUM(o.totalPrice) from Order o where o.userId = :userId")
	Double getTotalSpendByUser(Long userId);
	
	
	@Query("SELECT SUM(o.totalCarbon) from Order o where o.userId = :userId")
	Double getTotalCarbonByUser(Long userId);
	
	

}
