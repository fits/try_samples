package sample.commands;

import com.eventsourcing.EventStream;
import com.eventsourcing.StandardCommand;
import com.eventsourcing.layout.PropertyName;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

import sample.events.ItemsCheckedInToInventory;

@Value
@EqualsAndHashCode(callSuper = false)
public class CheckInItemsToInventory extends StandardCommand<Void, Void> {
    private UUID reference;
    private int count;

    public CheckInItemsToInventory(
            @PropertyName("reference") UUID reference,
            @PropertyName("count") int count) {

        this.reference = reference;
        this.count = count;
    }

    @Override
    public EventStream<Void> events() throws Exception {
        return EventStream.of(new ItemsCheckedInToInventory(reference, count));
    }
}
