package sample.commands;

import lombok.Value;

@Value
public class CheckInItemsToInventory {
    private long id;
    private int count;
}
