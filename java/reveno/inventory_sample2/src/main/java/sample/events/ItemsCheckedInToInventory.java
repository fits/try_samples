package sample.events;

import lombok.Value;

@Value
public class ItemsCheckedInToInventory {
    private long id;
    private int count;
}
