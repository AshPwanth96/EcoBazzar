package com.ecobazzar.ecobazzar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecobazzar.ecobazzar.dto.CartSummaryDto;
import com.ecobazzar.ecobazzar.model.CartItem;
import com.ecobazzar.ecobazzar.model.Product;
import com.ecobazzar.ecobazzar.repository.CartRepository;
import com.ecobazzar.ecobazzar.repository.ProductRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartItem addToCart(CartItem cartItem) {
        return cartRepository.save(cartItem);
    }

    public CartSummaryDto getCartSummary(Long userId) {

        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        double totalPrice = 0;
        double totalCarbonUsed = 0;
        double totalCarbonSaved = 0;
        String ecoSuggestion = null;

        for (CartItem item : cartItems) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            double carbon = product.getCarbonImpact() * item.getQuantity();
            totalCarbonUsed += carbon;
            totalPrice += product.getPrice() * item.getQuantity();

            // ONLY for non-eco products → find eco alternative
            if (!Boolean.TRUE.equals(product.getEcoCertified())) {

                String[] words = product.getName().split(" ");
                String keyword = words[words.length - 1].replaceAll("[^a-zA-Z]", "");

                Optional<Product> ecoAlt = productRepository
                        .findFirstByEcoCertifiedTrueAndNameContainingIgnoreCase(keyword);

                if (ecoAlt.isPresent()) {

                    double ecoCarbon = ecoAlt.get().getCarbonImpact();
                    double saved = (product.getCarbonImpact() - ecoCarbon) * item.getQuantity();

                    if (saved > 0) {
                        totalCarbonSaved += saved;

                        // only show suggestion once
                        if (ecoSuggestion == null) {
                            ecoSuggestion = "💡 Switch to " + ecoAlt.get().getName()
                                    + " and save " + String.format("%.2f", saved) + " kg CO₂!";
                        }
                    }
                }
            }
        }

        return new CartSummaryDto(
                cartItems,
                totalPrice,
                totalCarbonUsed,
                totalCarbonSaved,
                ecoSuggestion
        );
    }

    public void removeFromCart(Long id) {
        cartRepository.deleteById(id);
    }
}
