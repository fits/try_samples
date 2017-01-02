import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msemys.esjc.EventStoreBuilder;
import lombok.val;
import org.elder.sourcerer.DefaultAggregateRepository;
import org.elder.sourcerer.DefaultCommandFactory;
import org.elder.sourcerer.Operations;
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

        val aggregateRepository = new DefaultAggregateRepository<>(eventRepository,
                new InventoryProjection());

        val commandFactory = new DefaultCommandFactory<>(aggregateRepository);

        val createCommand = commandFactory.fromOperation(
                Operations.constructorOf(InventoryOperations::create));

        val checkInCommand = commandFactory.fromOperation(
                Operations.constructorOf(InventoryOperations::checkIn));

        val id = UUID.randomUUID().toString();

        System.out.println("**** id : " + id);

        val r1 = createCommand.setAggregateId(id).setArguments(new CreateInventoryItem("sample")).run();

        System.out.printf("%s, %s\n", r1.getEvents(), r1.getNewVersion());

        val r2 = checkInCommand.setAggregateId(id).setArguments(new CheckInItemsToInventory(5)).run();

        System.out.printf("%s, %s\n", r2.getEvents(), r2.getNewVersion());

        val r3 = checkInCommand.setAggregateId(id).setArguments(new CheckInItemsToInventory(3)).run();

        System.out.printf("%s, %s\n", r3.getEvents(), r3.getNewVersion());

        val aggregate = aggregateRepository.load(id);

        System.out.println("state: " + aggregate.state());

        eventStore.disconnect();
    }
}
