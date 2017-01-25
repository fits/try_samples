package sample.counter.impl;

import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;

import javax.inject.Inject;

import lombok.val;

import sample.counter.api.Counter;
import sample.counter.api.CounterService;
import sample.counter.api.CountMessage;

import sample.counter.impl.CounterCommand.*;

public class CounterServiceImpl implements CounterService {

    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    public CounterServiceImpl(
                PersistentEntityRegistry persistentEntityRegistry, 
                PubSubRegistry pubSub,
                Materializer materializer) {

        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(CounterEntity.class);

        val topic = pubSub.refFor(TopicId.of(CounterEvent.class, ""));

        topic.subscriber().runForeach(this::dumpEvent, materializer);
    }

    @Override
    public ServiceCall<NotUsed, Counter> create(String id) {
        return req -> getEntity(id).ask(new CreateCounter());
    }

    @Override
    public ServiceCall<NotUsed, Counter> find(String id) {
        return req -> getEntity(id).ask(new CurrentCounter());
    }

    @Override
    public ServiceCall<CountMessage, Counter> update(String id) {
        return req -> getEntity(id).ask(new UpdateCounter(req.getCount()));
    }

    private void dumpEvent(CounterEvent event) {
        System.out.println("*** pubsub event dump: " + event);
    }

    private PersistentEntityRef<CounterCommand> getEntity(String id) {
        return persistentEntityRegistry.refFor(CounterEntity.class, id);
    }
}
