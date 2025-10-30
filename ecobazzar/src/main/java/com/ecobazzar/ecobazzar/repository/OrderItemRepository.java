package com.ecobazzar.ecobazzar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecobazzar.ecobazzar.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
	@Query("SELECT oi from ORDERITEM oi JOIN Product p ON oi.productId = p.id WHERE p.sellerId = :sellerId")
	List<OrderItem> findBySellerId(Long sellerId);
	
	@Query("SELECT SUM(oi.quantity* p.price) FROM OrderItem oi Join Product p On oi.productId = p.id where p.sellerid = :sellerId")
	Double getTotalRevenueBySeller(Long sellerId);
	
	@Query("SELECT SUM(oi.quantity* p.carbonImpact) FROM OrderItem oi Join Product p On oi.productId = p.id where p.sellerid = :sellerId")
	Double getTotalCarbonBySeller(Long sellerId);

}
