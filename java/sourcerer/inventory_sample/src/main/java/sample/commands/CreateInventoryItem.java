package sample.commands;

import lombok.Value;

@Value
public class CreateInventoryItem {
    private String id;
    private String name;
}
