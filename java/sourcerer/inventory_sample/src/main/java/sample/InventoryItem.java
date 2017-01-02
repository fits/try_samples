package sample;

import lombok.Value;

@Value
public class InventoryItem {
	private String id;
    private String name;
    private int count;
}
