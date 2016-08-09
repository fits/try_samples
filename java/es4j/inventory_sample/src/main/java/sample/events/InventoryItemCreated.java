package sample.events;

import com.eventsourcing.StandardEvent;
import com.eventsourcing.index.SimpleIndex;

import java.util.UUID;

public class InventoryItemCreated extends StandardEvent {

    public static SimpleIndex<InventoryItemCreated, UUID> ID =
            (created, queryOptions) -> created.uuid();
}
