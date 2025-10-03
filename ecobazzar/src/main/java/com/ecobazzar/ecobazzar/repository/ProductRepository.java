package com.ecobazzar.ecobazzar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecobazzar.ecobazzar.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
