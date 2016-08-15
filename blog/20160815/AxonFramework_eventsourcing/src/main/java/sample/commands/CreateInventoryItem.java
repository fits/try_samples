package sample.commands;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CreateInventoryItem {
    @TargetAggregateIdentifier
    private String id;

    private String name;
}
