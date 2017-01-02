package sample.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemsCheckedInToInventory implements InventoryEvent {
	private int count;
}
