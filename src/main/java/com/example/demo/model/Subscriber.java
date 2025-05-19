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
	private User mobileNumber;
	private User name;
	private User email;
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

	public User getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(User mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public User getName() {
		return name;
	}

	public void setName(User name) {
		this.name = name;
	}

	public User getEmail() {
		return email;
	}

	public void setEmail(User email) {
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

    public void setMobileNumber(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMobileNumber'");
    }

    public void setEmail(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEmail'");
    }

    public void setName(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setName'");
    }
}