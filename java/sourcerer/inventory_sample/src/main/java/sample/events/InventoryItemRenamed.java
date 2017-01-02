package sample.events;

import lombok.Value;

@Value
public class InventoryItemRenamed implements InventoryEvent {
    private String name;
}
