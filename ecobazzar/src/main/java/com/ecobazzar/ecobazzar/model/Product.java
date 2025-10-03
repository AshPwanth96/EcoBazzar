package com.ecobazzar.ecobazzar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String details;
	
	private Double price;
	
	private Boolean ecoCertified;
	
	private Long sellerId;

	public Product(Long id, String name, String details, Double price, Boolean ecoCertified, Long sellerId) {
		super();
		this.id = id;
		this.name = name;
		this.details = details;
		this.price = price;
		this.ecoCertified = ecoCertified;
		this.sellerId = sellerId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getEcoCertified() {
		return ecoCertified;
	}

	public void setEcoCertified(Boolean ecoCertified) {
		this.ecoCertified = ecoCertified;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	
	
}
