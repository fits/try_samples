package sample;

import com.google.common.collect.ImmutableList;
import sample.commands.CheckInItemsToInventory;
import sample.commands.CreateInventoryItem;
import sample.events.InventoryEvent;
import sample.events.InventoryItemCreated;
import sample.events.InventoryItemRenamed;
import sample.events.ItemsCheckedInToInventory;

import java.util.List;

public class InventoryOperations {

    public static List<InventoryEvent> create(CreateInventoryItem cmd) {
        return ImmutableList.of(
            new InventoryItemCreated(cmd.getId()),
            new InventoryItemRenamed(cmd.getName())
        );
    }

    public static ItemsCheckedInToInventory checkIn(InventoryItem state, CheckInItemsToInventory cmd) {
        return new ItemsCheckedInToInventory(cmd.getCount());
    }
}
