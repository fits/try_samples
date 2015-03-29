package sample.model;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class Variation {
    private String size;
    private String color;

    public Variation() {
    }

    public Variation(String size, String color) {
        setSize(size);
        setColor(color);
    }

    public String getSize() {
        return size;
    }
    public void  setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}
