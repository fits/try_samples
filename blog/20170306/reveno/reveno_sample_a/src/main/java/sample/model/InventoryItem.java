package sample.model;

import lombok.Value;

@Value
public class InventoryItem {
    private String name;
    private int count;
}
