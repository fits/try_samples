package stock.reserve.model;

public class Reservation {
    private String rsvId;
    private int quantity;

    public Reservation() {}

    public Reservation(String rsvId, int quantity) {
        this.setRsvId(rsvId);
        this.setQuantity(quantity);
    }

    public String getRsvId() {
        return rsvId;
    }

    public void setRsvId(String rsvId) {
        this.rsvId = rsvId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
