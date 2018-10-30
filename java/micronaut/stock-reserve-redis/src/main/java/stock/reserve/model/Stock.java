package stock.reserve.model;

public class Stock {
    private String id;
    private int quantity;
    private int reserved;

    public Stock() {}

    public Stock(String stockId, int quantity, int reserved) {
        this.id = stockId;
        this.quantity = quantity;
        this.reserved = reserved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }
}
