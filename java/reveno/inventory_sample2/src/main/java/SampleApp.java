
import lombok.val;

import org.reveno.atp.core.Engine;
import org.reveno.atp.utils.MapUtils;

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

        reveno.domain().transaction("createInventoryItem", (t, ctx) -> {
            long id = t.id();
            String name = t.arg("name");

            ctx.repo().store(t.id(), new InventoryItem(name, 0));

            ctx.eventBus().publishEvent(new InventoryItemCreated(id));
            ctx.eventBus().publishEvent(new InventoryItemRenamed(id, name));

        }).uniqueIdFor(InventoryItem.class).command();

        reveno.domain().transaction("checkInItemsToInventory", (t, ctx) -> {
            long countId = t.longArg();
            int count = t.intArg("count");

            ctx.repo().remap(countId, InventoryItem.class, (id, state) ->
                    new InventoryItem(state.getName(), state.getCount() + count));

            ctx.eventBus().publishEvent(new ItemsCheckedInToInventory(countId, count));
        }).command();

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
}
