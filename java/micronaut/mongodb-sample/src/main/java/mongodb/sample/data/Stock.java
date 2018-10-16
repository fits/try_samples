package mongodb.sample.data;

public class Stock {
    private String id;
    private int quantity;

    public Stock() {}

    public Stock(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }
}
