package com.stock.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "stock_data")
public class Stock implements java.io.Serializable {
	
	private static final long serialVersionUID = 6705625035500971298L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@Column(name = "stock_id")
	private String stockId;
	
	@Column(name = "stock_name")
	private String stockName;
	
	@Column(name = "total_mart")
	private float totalMart;
	
	@Column(name = "current_mart")
	private float currentMart;
	
	@Column(name = "opt_id")
	private int optId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opt_id",insertable=false,updatable=false)
	private Opt opt;
	
	public Stock() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public float getTotalMart() {
		return totalMart;
	}

	public void setTotalMart(float totalMart) {
		this.totalMart = totalMart;
	}

	public float getCurrentMart() {
		return currentMart;
	}

	public void setCurrentMart(float currentMart) {
		this.currentMart = currentMart;
	}

	public Opt getOpt() {
		return opt;
	}

	public void setOpt(Opt opt) {
		this.opt = opt;
	}

	public int getOptId() {
		return optId;
	}

	public void setOptId(int optId) {
		this.optId = optId;
	}

	

}
