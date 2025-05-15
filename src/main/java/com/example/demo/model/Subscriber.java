package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "subscribers")
public class Subscriber {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String mobileNumber;
	private String name;
	private String email;
	@ManyToOne
	private Plan currentPlan;
	private LocalDate planExpiry;
	private Double dataUsed;
	private Double dataTotal;
	private LocalDate createdAt;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Plan getCurrentPlan() {
		return currentPlan;
	}

	public void setCurrentPlan(Plan currentPlan) {
		this.currentPlan = currentPlan;
	}

	public LocalDate getPlanExpiry() {
		return planExpiry;
	}

	public void setPlanExpiry(LocalDate planExpiry) {
		this.planExpiry = planExpiry;
	}

	public Double getDataUsed() {
		return dataUsed;
	}

	public void setDataUsed(Double dataUsed) {
		this.dataUsed = dataUsed;
	}

	public Double getDataTotal() {
		return dataTotal;
	}

	public void setDataTotal(Double dataTotal) {
		this.dataTotal = dataTotal;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
}