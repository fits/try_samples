package sample.protocols;

import com.eventsourcing.Protocol;
import com.eventsourcing.queries.ModelQueries;
import sample.events.InventoryItemRenamed;

public interface InventoryItemNameProtocol extends Protocol, ModelQueries {

    default String name() {
        return latestAssociatedEntity(
                InventoryItemRenamed.class,
                InventoryItemRenamed.REFERENCE_ID, InventoryItemRenamed.TIMESTAMP
        ).map(InventoryItemRenamed::getNewName).orElse("");
    }
}
