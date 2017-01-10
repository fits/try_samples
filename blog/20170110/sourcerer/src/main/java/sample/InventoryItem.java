package sample;

import lombok.Value;

@Value
public class InventoryItem {
    private String id;
    private String name;
    private int count;

    public InventoryItem withId(String newId) {
        return new InventoryItem(newId, name, count);
    }

    public InventoryItem withName(String newName) {
        return new InventoryItem(id, newName, count);
    }

    public InventoryItem withCount(int newCount) {
        return new InventoryItem(id, name, newCount);
    }
}
