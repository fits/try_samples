package sample.commands;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

@Value
public class CreateInventoryItem {
    @Wither @NonFinal private long id;
    private String name;
}
