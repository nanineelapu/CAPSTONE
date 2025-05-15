package com.example.demo.dto;

public class RechargeResponse {
	private String transactionId;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public RechargeResponse(String transactionId) {
		super();
		this.transactionId = transactionId;
	}

	
}
