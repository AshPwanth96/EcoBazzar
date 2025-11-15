package com.ecobazzar.ecobazzar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecobazzar.ecobazzar.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByEcoCertifiedTrue();

    List<Product> findByEcoCertifiedTrueOrderByCarbonImpactAsc();

    Optional<Product> findFirstByEcoCertifiedTrueAndNameContainingIgnoreCase(String namePart);

    List<Product> findByEcoRequestedTrue();
    
    List<Product> findBySellerId(Long sellerId);


    // No need to declare findById — JpaRepository already provides Optional<Product> findById(ID id);
}
