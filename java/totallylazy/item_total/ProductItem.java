
import java.math.BigDecimal;

public class ProductItem {
	private String id;
	private String name;
	private BigDecimal price;
	private int qty;

	public ProductItem(String id, String name, BigDecimal price, int qty) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.qty = qty;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public int getQty() {
		return qty;
	}
}

