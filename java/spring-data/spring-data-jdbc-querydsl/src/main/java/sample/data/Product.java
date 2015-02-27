package sample.data;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private long id;
    private String name;
    private BigDecimal price;
    private Timestamp releaseDate = new Timestamp(System.currentTimeMillis());

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }
}
