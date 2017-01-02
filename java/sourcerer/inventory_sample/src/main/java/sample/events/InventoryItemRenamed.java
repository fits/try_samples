package sample.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryItemRenamed implements InventoryEvent {
	private String name;
}
