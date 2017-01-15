package sample.inventory.api;

import com.lightbend.lagom.serialization.Jsonable;

import lombok.Value;

@Value
public class InventoryItem implements Jsonable {
    private String id;
    private String name;
    private int count;
}
