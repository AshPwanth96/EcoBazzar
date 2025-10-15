package com.ecobazzar.ecobazzar.dto;

import java.util.List;

import com.ecobazzar.ecobazzar.model.CartItem;

public class CartSummaryDto {

	private List<CartItem> items;
	
	private double totalPrice;
	
	private double totalCarbon;
	
	private String ecoSuggestion;
	
	public CartSummaryDto() {}
	

	public CartSummaryDto(List<CartItem> items, double totalPrice, double totalCarbon, String ecoSuggestion) {
		super();
		this.items = items;
		this.totalPrice = totalPrice;
		this.totalCarbon = totalCarbon;
		this.ecoSuggestion = ecoSuggestion;
	}

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getTotalCarbon() {
		return totalCarbon;
	}

	public void setTotalCarbon(double totalCarbon) {
		this.totalCarbon = totalCarbon;
	}

	public String getEcoSuggestion() {
		return ecoSuggestion;
	}

	public void setEcoSuggestion(String ecoSuggestion) {
		this.ecoSuggestion = ecoSuggestion;
	}
	
	
	
	
}
