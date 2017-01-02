package sample.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryItemCreated implements InventoryEvent {
	private String id;
}
