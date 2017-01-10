package sample.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.elder.sourcerer.EventType;

@EventType
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @Type(InventoryItemCreated.class),
    @Type(InventoryItemRenamed.class),
    @Type(ItemsCheckedInToInventory.class)
})
public interface InventoryEvent {
}
