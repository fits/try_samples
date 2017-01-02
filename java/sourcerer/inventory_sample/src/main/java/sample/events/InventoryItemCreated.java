package sample.events;

import lombok.Value;

@Value
public class InventoryItemCreated implements InventoryEvent {
	private String id;
}
