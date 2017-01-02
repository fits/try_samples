package sample;

import org.elder.sourcerer.AggregateProjection;
import org.jetbrains.annotations.NotNull;
import sample.events.InventoryItemCreated;
import sample.events.InventoryEvent;
import sample.events.InventoryItemRenamed;
import sample.events.ItemsCheckedInToInventory;

import static javaslang.API.*;
import static javaslang.Predicates.*;

public class InventoryProjection implements AggregateProjection<InventoryItem, InventoryEvent>{

    @NotNull
    @Override
    public InventoryItem empty() {
        return new InventoryItem("", "", 0);
    }

    @NotNull
    @Override
    public InventoryItem apply(@NotNull String id,
                               @NotNull InventoryItem state, @NotNull InventoryEvent event) {

        return Match(event).of(
            Case(instanceOf(InventoryItemCreated.class), ev ->
                    new InventoryItem(ev.getId(), state.getName(), state.getCount())),
            Case(instanceOf(InventoryItemRenamed.class), ev ->
                    new InventoryItem(state.getId(), ev.getName(), state.getCount())),
            Case(instanceOf(ItemsCheckedInToInventory.class), ev ->
                    new InventoryItem(state.getId(), state.getName(), state.getCount() + ev.getCount())),
            Case($(), state)
        );
    }
}
