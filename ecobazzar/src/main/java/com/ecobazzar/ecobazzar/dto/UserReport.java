package com.ecobazzar.ecobazzar.dto;

public class UserReport {

	private Long userId;
	
	private String userName;
	
	private Long totalPurchase;
	
	private double totalSpent;
	
	private double totalCarbonSaved;
	
	private String ecoBadge;
	
	public UserReport() {}

	public UserReport(Long userId, String userName, Long totalPurchase, double totalSpent, double totalCarbonSaved,
			String ecoBadge) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.totalPurchase = totalPurchase;
		this.totalSpent = totalSpent;
		this.totalCarbonSaved = totalCarbonSaved;
		this.ecoBadge = ecoBadge;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getTotalPurchase() {
		return totalPurchase;
	}

	public void setTotalPurchase(Long totalPurchase) {
		this.totalPurchase = totalPurchase;
	}

	public double getTotalSpent() {
		return totalSpent;
	}

	public void setTotalSpent(double totalSpent) {
		this.totalSpent = totalSpent;
	}

	public double getTotalCarbonSaved() {
		return totalCarbonSaved;
	}

	public void setTotalCarbonSaved(double totalCarbonSaved) {
		this.totalCarbonSaved = totalCarbonSaved;
	}

	public String getEcoBadge() {
		return ecoBadge;
	}

	public void setEcoBadge(String ecoBadge) {
		this.ecoBadge = ecoBadge;
	}
	
	
}
