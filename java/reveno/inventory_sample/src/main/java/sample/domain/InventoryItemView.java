package sample.domain;

import lombok.Value;

@Value
public class InventoryItemView {
	private long id;
	private String name;
	private int count;
}
