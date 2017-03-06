
import lombok.val;

import org.reveno.atp.core.Engine;
import org.reveno.atp.utils.MapUtils;

import sample.model.InventoryItem;
import sample.model.InventoryItemView;
import sample.events.InventoryItemCreated;
import sample.events.InventoryItemRenamed;
import sample.events.ItemsCheckedInToInventory;

public class SampleApp {
    public static void main(String... args) {
        val reveno = new Engine("db");

        setUp(reveno);

        reveno.startup();

        long id = reveno.executeSync("createInventoryItem",
                MapUtils.map("name", "sample1"));

        System.out.println("id: " + id);

        reveno.executeSync("checkInItemsToInventory", MapUtils.map("id", id, "count", 5));
        reveno.executeSync("checkInItemsToInventory", MapUtils.map("id", id, "count", 3));

        val res = reveno.query().find(InventoryItemView.class, id);

        System.out.println("result: " + res);

        reveno.shutdown();
    }

    private static void setUp(Engine reveno) {
        reveno.domain().viewMapper(
            InventoryItem.class,
            InventoryItemView.class,
            (id, e, r) -> new InventoryItemView(id, e.getName(), e.getCount())
        );

        reveno.domain().transaction("createInventoryItem", (t, ctx) -> {
            long id = t.id();
            String name = t.arg("name");

            ctx.repo().store(id, new InventoryItem(name, 0));

            ctx.eventBus().publishEvent(new InventoryItemCreated(id));
            ctx.eventBus().publishEvent(new InventoryItemRenamed(id, name));

        }).uniqueIdFor(InventoryItem.class).command();

        reveno.domain().transaction("checkInItemsToInventory", (t, ctx) -> {
            long id = t.longArg("id");
            int count = t.intArg("count");

            ctx.repo().remap(id, InventoryItem.class, (rid, state) ->
                    new InventoryItem(state.getName(), state.getCount() + count));

            ctx.eventBus().publishEvent(new ItemsCheckedInToInventory(id, count));
        }).command();

        reveno.events().eventHandler(InventoryItemCreated.class, (event, meta) ->
                System.out.println("*** create event: " + event +
                        ", transactionTime: " + meta.getTransactionTime() +
                        ", isRestore: " + meta.isRestore()));
    }
}
