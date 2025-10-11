package com.ecobazzar.ecobazzar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecobazzar.ecobazzar.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	List<Product> findByEcoCertifiedTrue();
	
    List<Product> findByEcoCertifiedTrueOrderByCarbonImpactAsc();

}
