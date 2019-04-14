package com.kakaoPay.housingFinance.finance.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "support")
@JsonInclude(Include.NON_NULL)
public class Support {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "bank")
	private String bank;

	@Column(name = "year")
	private Integer year;

	@Column(name = "month")
	private Integer month;

	@Column(name = "amount")
	private Long amount;

	@Transient
	private Long totalAmount;
	
	@Transient
	private Double averageAmount;
	
	public Support() {
		
	};

	public Support(Integer year, Long totalAmount) {
		this.year = year;
		this.totalAmount = totalAmount;
	}

	public Support(String bank, Long totalAmount) {
		this.bank = bank;
		this.totalAmount = totalAmount;
	}
	
	public Support(Integer year, String bank) {
		this.year = year;
		this.bank = bank;
	}
	
	public Support(Integer year, Double averageAmount) {
		this.year = year;
		this.averageAmount = averageAmount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getAverageAmount() {
		return averageAmount;
	}

	public void setAverageAmount(Double averageAmount) {
		this.averageAmount = averageAmount;
	}
	
	
}
