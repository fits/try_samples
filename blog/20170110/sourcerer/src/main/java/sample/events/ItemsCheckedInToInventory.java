package sample.events;

import lombok.Value;

@Value
public class ItemsCheckedInToInventory implements InventoryEvent {
    private int count;
}
