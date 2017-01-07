package sample.commands;

import com.eventsourcing.EventStream;
import com.eventsourcing.Repository;
import com.eventsourcing.StandardCommand;
import com.eventsourcing.layout.PropertyName;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.val;

import sample.domain.InventoryItem;
import sample.events.InventoryItemCreated;
import sample.events.InventoryItemRenamed;

@Value
@EqualsAndHashCode(callSuper = false)
public class CreateInventoryItem extends StandardCommand<InventoryItemCreated, InventoryItem> {
    private String name;

    public CreateInventoryItem(@PropertyName("name") String name) {
        this.name = name;
    }

    @Override
    public EventStream<InventoryItemCreated> events() throws Exception {
        val created = new InventoryItemCreated();
        val renamed = new InventoryItemRenamed(created.uuid(), name);

        return EventStream.ofWithState(created, created, renamed);
    }

    @Override
    public InventoryItem result(InventoryItemCreated state, Repository repository) {
        return InventoryItem.lookup(repository, state.uuid()).get();
    }
}
