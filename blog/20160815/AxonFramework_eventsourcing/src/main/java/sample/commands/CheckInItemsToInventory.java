package sample.commands;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class CheckInItemsToInventory {

    @TargetAggregateIdentifier
    private String id;

    private int count;
}
