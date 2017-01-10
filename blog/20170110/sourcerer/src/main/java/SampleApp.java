
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msemys.esjc.EventStoreBuilder;

import lombok.val;

import org.elder.sourcerer.*;
import org.elder.sourcerer.esjc.EventStoreEsjcEventRepositoryFactory;

import sample.*;
import sample.commands.CheckInItemsToInventory;
import sample.commands.CreateInventoryItem;
import sample.events.InventoryEvent;

import java.util.UUID;

public class SampleApp {
    public static void main(String... args) throws JsonProcessingException {

        val eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("localhost", 1113)
                .build();

        val mapper = new ObjectMapper();

        val eventRepositoryFactory = new EventStoreEsjcEventRepositoryFactory(
                eventStore, mapper, "sample");

        val eventRepository = eventRepositoryFactory.getEventRepository(InventoryEvent.class);

        val aggregateRepository = new DefaultAggregateRepository<>(
                eventRepository, 
                new InventoryProjection()
        );

        val commandFactory = new DefaultCommandFactory<>(aggregateRepository);

        val createCommand = commandFactory.fromOperation(
                Operations.constructorOf(InventoryOperations::create));

        val checkInCommand = commandFactory.fromOperation(
                Operations.updateOf(InventoryOperations::checkIn));

        val id = UUID.randomUUID().toString();

        val r1 = createCommand.setAggregateId(id)
                .setArguments(new CreateInventoryItem(id, "sample"))
                .run();

        printCommandResult(r1);

        val r2 = checkInCommand.setAggregateId(id)
                .setArguments(new CheckInItemsToInventory(5))
                .run();

        printCommandResult(r2);

        val r3 = checkInCommand.setAggregateId(id)
                .setArguments(new CheckInItemsToInventory(3))
                .run();

        printCommandResult(r3);

        val aggregate = aggregateRepository.load(id);

        System.out.println("*** result state: " + aggregate.state());

        eventStore.disconnect();
    }

    private static void printCommandResult(CommandResult<? extends InventoryEvent> result) {
        System.out.printf(
            "*** command result: events=%s, newVersion=%s\n", 
            result.getEvents(), 
            result.getNewVersion()
        );
    }
}
