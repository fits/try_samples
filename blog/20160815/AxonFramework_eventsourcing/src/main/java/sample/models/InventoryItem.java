package sample.models;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.commandhandling.model.ApplyMore;

import org.axonframework.eventsourcing.EventSourcingHandler;

import lombok.Getter;
import lombok.ToString;

import sample.commands.CreateInventoryItem;
import sample.commands.CheckInItemsToInventory;
import sample.events.InventoryItemCreated;
import sample.events.InventoryItemRenamed;
import sample.events.ItemsCheckedInToInventory;

@ToString
public class InventoryItem {

    @AggregateIdentifier
    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private int count;

    public InventoryItem() {
    }

    @CommandHandler
    public InventoryItem(CreateInventoryItem cmd) {
        System.out.println("C call new: " + cmd);

        AggregateLifecycle.apply(new InventoryItemCreated(cmd.getId()));
        AggregateLifecycle.apply(new InventoryItemRenamed(cmd.getName()));
    }

    @CommandHandler
    private ApplyMore updateCount(CheckInItemsToInventory cmd) {
        System.out.println("C call updateCount: " + cmd);

        return AggregateLifecycle.apply(new ItemsCheckedInToInventory(cmd.getCount()));
    }

    @EventSourcingHandler
    private void applyCreated(InventoryItemCreated event) {
        System.out.println("E call applyCreated: " + event);

        this.id = event.getId();
    }

    @EventSourcingHandler
    private void applyRenamed(InventoryItemRenamed event) {
        System.out.println("E call applyRenamed: " + event);

        this.name = event.getNewName();
    }

    @EventSourcingHandler
    private void applyCheckedIn(ItemsCheckedInToInventory event) {
        System.out.println("E call applyCheckedIn: " + event);

        this.count += event.getCount();
    }
}
