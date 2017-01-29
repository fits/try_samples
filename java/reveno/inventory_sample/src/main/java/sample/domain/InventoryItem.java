package sample.domain;

import lombok.Value;

@Value
public class InventoryItem {
    private String name;
    private int count;
}
