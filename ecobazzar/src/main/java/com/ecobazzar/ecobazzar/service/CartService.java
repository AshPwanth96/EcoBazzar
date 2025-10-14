package com.ecobazzar.ecobazzar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecobazzar.ecobazzar.model.CartItem;
import com.ecobazzar.ecobazzar.repository.CartRepository;

@Service
public class CartService {
	
	private final CartRepository cartRepository;
	
	public CartService(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}
	
	public CartItem addToCart(CartItem cartItem) {
		return cartRepository.save(cartItem);
	}
	
	public List<CartItem> getCartByUserId(Long userId){
		return cartRepository.findByUserId(userId);
	}
	
	public void removeFromCart(Long id) {
		cartRepository.deleteById(id);
	}

}
