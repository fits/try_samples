package sample.inventory.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import lombok.val;
import sample.inventory.api.InventoryItem;

import sample.inventory.impl.InventoryCommand.*;
import sample.inventory.impl.InventoryEvent.*;

import java.util.Arrays;
import java.util.Optional;

public class InventoryEntity extends PersistentEntity<InventoryCommand, InventoryEvent, InventoryItem> {
    @Override
    public Behavior initialBehavior(Optional<InventoryItem> snapshotState) {

        val bhvBuilder = newBehaviorBuilder(
                snapshotState.orElse(new InventoryItem("", "", 0)));

        bhvBuilder.setCommandHandler(CreateInventoryItem.class, (cmd, ctx) -> {
            System.out.println("*** command handle : " + cmd);

            val events = Arrays.asList(
                    new InventoryItemCreated(cmd.getId()),
                    new InventoryItemRenamed(cmd.getName())
            );

            return ctx.thenPersistAll(events, () -> ctx.reply(Done.getInstance()));
        });

        bhvBuilder.setCommandHandler(CheckInItemsToInventory.class, (cmd, ctx) -> {
            System.out.println("*** command handle : " + cmd);
            return ctx.thenPersist(new ItemsCheckedInToInventory(cmd.getCount()), ev -> ctx.reply(Done.getInstance()));
        });



        bhvBuilder.setEventHandler(InventoryItemCreated.class, ev ->
                new InventoryItem(ev.getId(), "", 0));

        bhvBuilder.setEventHandler(InventoryItemRenamed.class, ev ->
                new InventoryItem(state().getId(), ev.getName(), state().getCount()));

        bhvBuilder.setEventHandler(ItemsCheckedInToInventory.class, ev ->
                new InventoryItem(
                        state().getId(),
                        state().getName(),
                        state().getCount() + ev.getCount()));

        bhvBuilder.setReadOnlyCommandHandler(CurrentState.class, (cmd, ctx) -> ctx.reply(state()));

        return bhvBuilder.build();
    }
}
