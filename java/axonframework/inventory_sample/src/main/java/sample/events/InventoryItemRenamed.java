package sample.events;

import lombok.Value;

@Value
public class InventoryItemRenamed {
    private String newName;
}
