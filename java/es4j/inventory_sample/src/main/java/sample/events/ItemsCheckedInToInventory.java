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
public class ItemsCheckedInToInventory extends StandardEvent {
    @Getter
    private final UUID reference;
    @Getter
    private final int count;

    public ItemsCheckedInToInventory(
            @PropertyName("reference") UUID reference,
            @PropertyName("count") int count) {

        this.reference = reference;
        this.count = count;
    }

    public static SimpleIndex<ItemsCheckedInToInventory, UUID> ID =
            (checkedIn, queryOptions) -> checkedIn.uuid();

    public static SimpleIndex<ItemsCheckedInToInventory, UUID> REFERENCE_ID =
            (checkedIn, queryOptions) -> checkedIn.getReference();

    @Index({EQ, LT, GT})
    public static SimpleIndex<ItemsCheckedInToInventory, HybridTimestamp> TIMESTAMP =
            (checkedIn, queryOptions) -> checkedIn.timestamp();
}
