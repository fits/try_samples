
import lombok.val;

import org.reveno.atp.core.Engine;

import sample.commands.CheckInItemsToInventory;
import sample.commands.CreateInventoryItem;
import sample.domain.InventoryItem;
import sample.domain.InventoryItemView;
import sample.events.InventoryItemCreated;
import sample.events.InventoryItemRenamed;
import sample.events.ItemsCheckedInToInventory;

public class SampleApp {
    public static void main(String... args) {
        val reveno = new Engine("db");

        reveno.config().mutableModel();

        reveno.domain().viewMapper(
            InventoryItem.class,
            InventoryItemView.class,
            (id, e, r) -> new InventoryItemView(id, e.getName(), e.getCount())
        );

        reveno.domain().command(CreateInventoryItem.class, Long.class, (cmd, ctx) -> {
                long id = ctx.id(InventoryItem.class);
                ctx.executeTxAction(cmd.withId(id));
                return id;
        });

        reveno.domain().command(CheckInItemsToInventory.class, (cmd, ctx) -> ctx.executeTxAction(cmd));

        reveno.domain().transactionAction(CreateInventoryItem.class, (act, ctx) -> {
            ctx.repo().store(act.getId(), new InventoryItem(act.getName(), 0));

            ctx.eventBus().publishEvent(new InventoryItemCreated(act.getId()));
            ctx.eventBus().publishEvent(new InventoryItemRenamed(act.getId(), act.getName()));
        });

        reveno.domain().transactionAction(CheckInItemsToInventory.class, (act, ctx) -> {
            ctx.repo().remap(act.getId(), InventoryItem.class, (id, state) ->
                    new InventoryItem(state.getName(), state.getCount() + act.getCount()));

            ctx.eventBus().publishEvent(new ItemsCheckedInToInventory(act.getId(), act.getCount()));
        });

        reveno.startup();

        long id = reveno.executeSync(new CreateInventoryItem(0, "sample1"));

        System.out.println(id);

        reveno.executeSync(new CheckInItemsToInventory(id, 5));
        reveno.executeSync(new CheckInItemsToInventory(id, 3));

        val res = reveno.query().find(InventoryItemView.class, id);

        System.out.println(res);

        reveno.shutdown();
    }
}
