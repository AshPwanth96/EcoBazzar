package com.ecobazzar.ecobazzar.controller;

import com.ecobazzar.ecobazzar.dto.CartSummaryDto;
import com.ecobazzar.ecobazzar.model.CartItem;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.UserRepository;
import com.ecobazzar.ecobazzar.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public CartItem addToCart(@RequestBody CartItem cartItem, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartItem.setUserId(user.getId());
        return cartService.addToCart(cartItem);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/summary")
    public CartSummaryDto getCartSummary(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartService.getCartSummary(user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public String removeFromCart(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.removeFromCart(id, user.getId());
        return "Item removed from cart";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/swap")
    public void swapToEco(@RequestBody SwapRequest request, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.swapToEco(user.getId(), request.cartItemId(), request.newProductId());
    }
}

record SwapRequest(Long cartItemId, Long newProductId) {}