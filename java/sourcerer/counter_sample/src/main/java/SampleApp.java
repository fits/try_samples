import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.msemys.esjc.EventStoreBuilder;
import lombok.val;
import org.elder.sourcerer.DefaultAggregateRepository;
import org.elder.sourcerer.DefaultCommandFactory;
import org.elder.sourcerer.Operations;
import org.elder.sourcerer.esjc.EventStoreEsjcEventRepositoryFactory;

import sample.*;

public class SampleApp {
    public static void main(String... args) throws JsonProcessingException {

        val eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("localhost", 1113)
                .build();

        val mapper = new ObjectMapper();

        val eventRepositoryFactory = new EventStoreEsjcEventRepositoryFactory(
                eventStore, mapper, "sample");

        val eventRepository = eventRepositoryFactory.getEventRepository(AddedCounter.class);

        val aggregateRepository = new DefaultAggregateRepository<>(
                eventRepository,
                new CounterProjection());

        val commandFactory = new DefaultCommandFactory<>(aggregateRepository);

        val addCommand = commandFactory.fromOperation(
                Operations.constructorOf(CounterOperations::add));

        val id = "sample1";

        val r1 = addCommand.setAggregateId(id).setArguments(new CounterAdd(3)).run();
        val r2 = addCommand.setAggregateId(id).setArguments(new CounterAdd(5)).run();

        System.out.printf("%s, %s\n", r1.getEvents(), r1.getNewVersion());
        System.out.printf("%s, %s\n", r2.getEvents(), r2.getNewVersion());

        val aggregate = aggregateRepository.load(id);

        System.out.printf("state = %d\n", aggregate.state());

        eventStore.disconnect();
    }
}
