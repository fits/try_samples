package sample.events;

import static com.eventsourcing.index.IndexEngine.IndexFeature.*;

import com.eventsourcing.StandardEvent;
import com.eventsourcing.hlc.HybridTimestamp;
import com.eventsourcing.index.Index;
import com.eventsourcing.index.SimpleIndex;
import com.eventsourcing.layout.PropertyName;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = false)
public class InventoryItemRenamed extends StandardEvent {
    private UUID reference;
    private String newName;

    public InventoryItemRenamed(@PropertyName("reference") UUID reference,
                                @PropertyName("newName") String newName) {

        this.reference = reference;
        this.newName = newName;
    }

    public static SimpleIndex<InventoryItemRenamed, UUID> ID =
            SimpleIndex.as(InventoryItemRenamed::uuid);

    public static SimpleIndex<InventoryItemRenamed, UUID> REFERENCE_ID =
            SimpleIndex.as(InventoryItemRenamed::getReference);

    @Index({EQ, LT, GT})
    public static SimpleIndex<InventoryItemRenamed, HybridTimestamp> TIMESTAMP =
            SimpleIndex.as(InventoryItemRenamed::timestamp);
}
