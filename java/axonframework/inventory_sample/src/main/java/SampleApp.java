
import org.axonframework.commandhandling.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;

import org.axonframework.eventsourcing.EventSourcedAggregate;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;

// for SnapshotterTrigger
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.eventsourcing.DomainEventMessage;
import org.axonframework.eventsourcing.SnapshotterTrigger;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import java.util.List;

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
        repository.setSnapshotterTrigger(new SampleSnapshotterTrigger());

        new AggregateAnnotationCommandHandler<>(InventoryItem.class, 
                                                repository).subscribe(cmdBus);

        String r1 = gateway.sendAndWait(new CreateInventoryItem("s1", "sample1"));
        System.out.println("id: " + r1);

        System.out.println("----------");

        EventSourcedAggregate<InventoryItem> r2 = 
            gateway.sendAndWait(new CheckInItemsToInventory("s1", 5));

        printAggregate(r2);

        System.out.println("----------");

        gateway.sendAndWait(new CreateInventoryItem("s1", "sample3"));

        System.out.println("----------");


        EventSourcedAggregate<InventoryItem> r3 = 
            gateway.sendAndWait(new CheckInItemsToInventory("s1", 3));

        printAggregate(r3);
    }

    private static void printAggregate(EventSourcedAggregate<InventoryItem> esa) {
        System.out.println(esa.getAggregateRoot());
    }


    static class SampleSnapshotterTrigger implements SnapshotterTrigger {
        @Override
        public DomainEventStream decorateForRead(String aggregateIdentifier, DomainEventStream eventStream) {

            System.out.println("*** call decorateForRead : " + aggregateIdentifier);

            return eventStream;
        }

        @Override
        public List<DomainEventMessage<?>> decorateForAppend(Aggregate<?> aggregate, List<DomainEventMessage<?>> events) {

            System.out.println("*** call decorateForAppend");

            return events;
        }
    }
}
