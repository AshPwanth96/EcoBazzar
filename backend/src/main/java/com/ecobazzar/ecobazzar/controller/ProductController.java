package com.ecobazzar.ecobazzar.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ecobazzar.ecobazzar.model.Product;
import com.ecobazzar.ecobazzar.model.User;
import com.ecobazzar.ecobazzar.repository.UserRepository;
import com.ecobazzar.ecobazzar.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Seller not found"));
        product.setSellerId(seller.getId());
        return productService.createProduct(product);
    }

 
 // public marketplace -> show ALL products (including non-certified)
    @GetMapping
    public List<Product> listAllProducts() {
        return productService.getAllProducts();
    }


    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @GetMapping("/seller")
    public List<Product> listSellerProducts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Seller not found"));
        return productService.getProductsBySellerId(seller.getId());
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @PutMapping("/{id}")
    public Product updateProductDetails(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProductDetails(id, product);
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProductDetails(@PathVariable Long id) {
        productService.deleteProductDetails(id);
    }
}



