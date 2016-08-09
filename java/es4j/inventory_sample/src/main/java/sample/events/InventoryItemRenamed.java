package sample.events;

import static com.eventsourcing.index.IndexEngine.IndexFeature.*;

import com.eventsourcing.StandardEvent;
import com.eventsourcing.hlc.HybridTimestamp;
import com.eventsourcing.index.Index;
import com.eventsourcing.index.SimpleIndex;
import com.eventsourcing.layout.PropertyName;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
public class InventoryItemRenamed extends StandardEvent {
    @Getter
    private UUID reference;
    @Getter
    private String newName;

    public InventoryItemRenamed(@PropertyName("reference") UUID reference,
                                @PropertyName("newName") String newName) {

        this.reference = reference;
        this.newName = newName;
    }

    public static SimpleIndex<InventoryItemRenamed, UUID> ID =
            (renamed, queryOptions) -> renamed.uuid();

    public static SimpleIndex<InventoryItemRenamed, UUID> REFERENCE_ID =
            (renamed, queryOptions) -> renamed.getReference();

    @Index({EQ, LT, GT})
    public static SimpleIndex<InventoryItemRenamed, HybridTimestamp> TIMESTAMP =
            (renamed, queryOptions) -> renamed.timestamp();
}
