package sample.commands;

import com.eventsourcing.EventStream;
import com.eventsourcing.StandardCommand;
import com.eventsourcing.layout.PropertyName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

import sample.events.ItemsCheckedInToInventory;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
public class CheckInItemsToInventory extends StandardCommand<Void, Void> {
    @Getter
    private final UUID inventoryItemId;
    @Getter
    private final int count;

    public CheckInItemsToInventory(
            @PropertyName("inventoryItemId") UUID inventoryItemId,
            @PropertyName("count") int count) {

        if (count <= 0) {
            throw new IllegalArgumentException("count > 0");
        }

        this.inventoryItemId = inventoryItemId;
        this.count = count;
    }

    @Override
    public EventStream<Void> events() throws Exception {
        val checkedIn = new ItemsCheckedInToInventory(inventoryItemId, count);

        return EventStream.of(checkedIn);
    }
}
