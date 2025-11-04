package com.ecobazzar.ecobazzar.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import com.ecobazzar.ecobazzar.model.CartItem;
import com.ecobazzar.ecobazzar.model.Order;
import com.ecobazzar.ecobazzar.model.OrderItem;
import com.ecobazzar.ecobazzar.model.Product;
import com.ecobazzar.ecobazzar.repository.CartRepository;
import com.ecobazzar.ecobazzar.repository.OrderItemRepository;
import com.ecobazzar.ecobazzar.repository.OrderRepository;
import com.ecobazzar.ecobazzar.repository.ProductRepository;

@Service
public class OrderService {
	
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	
	public OrderService(
	        CartRepository cartRepository, 
	        ProductRepository productRepository, 
	        OrderRepository orderRepository,
	        OrderItemRepository orderItemRepository) {
		
		this.cartRepository  = cartRepository;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}
	
	@Transactional
	public Order checkout(Long userId) {
	    // 🛒 Step 1: Get user's cart
		List<CartItem> cartItems = cartRepository.findByUserId(userId);
		
		if (cartItems.isEmpty()) {
			throw new RuntimeException("Cart is Empty! Cannot Checkout");
		}
			
		double totalPrice = 0;
		double totalCarbon = 0;
			
		// 🧾 Step 2: Calculate totals
		for (CartItem item : cartItems) {
			Product product = productRepository.findById(item.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found"));
			
			totalPrice += product.getPrice() * item.getQuantity();
			totalCarbon += product.getCarbonImpact() * item.getQuantity();
		}
			
		// 🧾 Step 3: Create the order
		Order order = new Order(null, userId, LocalDate.now(), totalPrice, totalCarbon);
		Order savedOrder = orderRepository.save(order);

		// 🧾 Step 4: Save individual order items for seller reports
		for (CartItem item : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(savedOrder.getId());
			orderItem.setProductId(item.getProductId());
			orderItem.setQuantity(item.getQuantity());
			orderItemRepository.save(orderItem);
		}

		// 🧹 Step 5: Clear the user's cart
		cartRepository.deleteAll(cartItems);

		// ✅ Step 6: Return order
		return savedOrder;
	}
	 
	public List<Order> getOrdersByUserId(Long userId) {
		return orderRepository.findByUserId(userId);
	}
}
