package sample;

import java.math.BigDecimal;

class Item {
	private int code;
	private BigDecimal price = BigDecimal.ZERO;

	public Item(int code) {
		this.code = code;
	}

	public Item(int code, BigDecimal price) {
		this.code = code;
		this.price = price;
	}

	public int getCode() {
		return code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "Item(code=" + code + ")";
	}
}
