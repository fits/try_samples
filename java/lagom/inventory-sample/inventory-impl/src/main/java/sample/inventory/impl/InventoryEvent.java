package sample.inventory.impl;

import com.lightbend.lagom.serialization.Jsonable;

import lombok.Value;

public interface InventoryEvent extends Jsonable {
    @Value
    public class InventoryItemCreated implements InventoryEvent {
        private String id;
    }

    @Value
    public class InventoryItemRenamed implements InventoryEvent {
        private String name;
    }

    @Value
    public class ItemsCheckedInToInventory implements InventoryEvent {
        private int count;
    }
}
