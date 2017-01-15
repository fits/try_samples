package sample.inventory.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;
import lombok.Value;
import sample.inventory.api.InventoryItem;

public interface InventoryCommand extends Jsonable {
    @Value
    public final class CreateInventoryItem implements InventoryCommand,
            PersistentEntity.ReplyType<Done> {

        private String id;
        private String name;
    }

    @Value
    public final class CheckInItemsToInventory implements InventoryCommand,
            PersistentEntity.ReplyType<Done> {

        private int count;
    }

    @Value
    public final class CurrentState implements InventoryCommand,
            PersistentEntity.ReplyType<InventoryItem> {
    }
}
