package sample.events;

import com.eventsourcing.StandardEvent;
import com.eventsourcing.index.SimpleIndex;
import com.eventsourcing.layout.PropertyName;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.UUID;

@Value
@EqualsAndHashCode(callSuper = false)
public class ItemsCheckedInToInventory extends StandardEvent {
    private UUID reference;
    private int count;

    public ItemsCheckedInToInventory(
            @PropertyName("reference") UUID reference,
            @PropertyName("count") int count) {

        this.reference = reference;
        this.count = count;
    }

    public static SimpleIndex<ItemsCheckedInToInventory, UUID> ID =
            SimpleIndex.as(ItemsCheckedInToInventory::uuid);

    public static SimpleIndex<ItemsCheckedInToInventory, UUID> REFERENCE_ID =
            SimpleIndex.as(ItemsCheckedInToInventory::getReference);
}
