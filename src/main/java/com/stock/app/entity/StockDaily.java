package com.stock.app.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "stock_daily")
public class StockDaily {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name = "stock_id")
	private String stockId;
	
	@Column(name = "trade_date")
	private Long tradeDate;
	
	@Column(name = "max_value")
	private float maxValue;
	
	@Column(name = "min_value")
	private float minValue;
	
	@Column(name = "open_value")
	private float openValue;
	
	@Column(name = "close_value")
	private float closeValue;
	
	@Column(name = "trade_vol")
	private long tradeVol;
	
	@Column(name = "trade_sum")
	private long tradeSum;

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

	public Long getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Long tradeDate) {
		this.tradeDate = tradeDate;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getOpenValue() {
		return openValue;
	}

	public void setOpenValue(float openValue) {
		this.openValue = openValue;
	}

	public float getCloseValue() {
		return closeValue;
	}

	public void setCloseValue(float closeValue) {
		this.closeValue = closeValue;
	}

	public long getTradeVol() {
		return tradeVol;
	}

	public void setTradeVol(long tradeVol) {
		this.tradeVol = tradeVol;
	}

	public long getTradeSum() {
		return tradeSum;
	}

	public void setTradeSum(long tradeSum) {
		this.tradeSum = tradeSum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockDaily other = (StockDaily) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	
	
}
