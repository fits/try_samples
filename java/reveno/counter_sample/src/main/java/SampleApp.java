
import lombok.val;

import org.reveno.atp.core.Engine;

import sample.Counter;
import sample.CounterView;

import static org.reveno.atp.utils.MapUtils.map;

public class SampleApp {
    public static void main(String... args) {
        val reveno = new Engine("db");

        reveno.config().mutableModel();

        reveno.domain().viewMapper(
            Counter.class, 
            CounterView.class,
            (id, e, r) -> new CounterView(id, e.getCount())
        );

        reveno.domain()
            .transaction("create", (t, c) -> {
                System.out.println("call create: id=" + t.id());
                c.repo().store(t.id(), new Counter());
            })
            .uniqueIdFor(Counter.class)
            .command();

        reveno.domain()
            .transaction("countUp", (t, c) -> {
                val d = c.repo().get(Counter.class, t.arg());
                d.setCount(d.getCount() + t.intArg("value"));

                System.out.println("call countUp: " + d);
            })
            .command();

        reveno.startup();

        long id = reveno.executeSync("create");

        System.out.println(id);

        reveno.executeSync("countUp", map("id", id, "value", 5));
        reveno.executeSync("countUp", map("id", id, "value", 3));

        val res = reveno.query().find(CounterView.class, id);

        System.out.println(res);

        reveno.shutdown();
    }
}
