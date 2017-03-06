package sample.model;

import lombok.Value;

@Value
public class InventoryItemView {
	private long id;
	private String name;
	private int count;
}
