package sample.events;

import lombok.Value;

@Value
public class InventoryItemRenamed {
    private long id;
    private String newName;
}
