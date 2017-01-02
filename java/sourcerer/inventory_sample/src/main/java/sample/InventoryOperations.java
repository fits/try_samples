package sample;

import sample.commands.CheckInItemsToInventory;
import sample.commands.CreateInventoryItem;
import sample.events.InventoryEvent;
import sample.events.InventoryItemCreated;
import sample.events.InventoryItemRenamed;
import sample.events.ItemsCheckedInToInventory;

import java.util.Arrays;
import java.util.List;

public class InventoryOperations {

    public static List<InventoryEvent> create(CreateInventoryItem cmd) {
        return Arrays.asList(new InventoryItemCreated(), new InventoryItemRenamed(cmd.getName()));
    }

    public static ItemsCheckedInToInventory checkIn(CheckInItemsToInventory cmd) {
        return new ItemsCheckedInToInventory(cmd.getCount());
    }
}
