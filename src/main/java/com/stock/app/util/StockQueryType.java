package com.stock.app.util;

public enum StockQueryType {
	TRENDUP(1),
	TRENDDOWN(2),
	TOPRATE(3),
	BOTTOMRATE(4);
	private int type;
	private StockQueryType(int type) {
		this.type=type;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return getName();
	}
	
	public String getName() {
		return this.name();
	}
}

 