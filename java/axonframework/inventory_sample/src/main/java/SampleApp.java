
import org.axonframework.commandhandling.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;

import org.axonframework.eventsourcing.EventSourcedAggregate;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;

import lombok.val;

import sample.commands.CreateInventoryItem;
import sample.commands.CheckInItemsToInventory;
import sample.domain.InventoryItem;

public class SampleApp {
    public static void main(String... args) {

        val cmdBus = new SimpleCommandBus();
        val gateway = new DefaultCommandGateway(cmdBus);

        val es = new EmbeddedEventStore(new InMemoryEventStorageEngine());

        val repository = new EventSourcingRepository<>(InventoryItem.class, es);

        new AggregateAnnotationCommandHandler<>(InventoryItem.class, 
                                                repository).subscribe(cmdBus);

        String r1 = gateway.sendAndWait(new CreateInventoryItem("s1", "sample1"));
        System.out.println("id: " + r1);

        System.out.println("----------");

        EventSourcedAggregate<InventoryItem> r2 = 
            gateway.sendAndWait(new CheckInItemsToInventory("s1", 5));

        printAggregate(r2);

        System.out.println("----------");

        EventSourcedAggregate<InventoryItem> r3 = 
            gateway.sendAndWait(new CheckInItemsToInventory("s1", 3));

        printAggregate(r3);
    }

    private static void printAggregate(EventSourcedAggregate<InventoryItem> esa) {
        System.out.println(esa.getAggregateRoot());
    }
}
