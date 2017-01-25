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

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import sample.counter.api.Counter;
import sample.counter.api.CounterService;
import sample.counter.api.CountMessage;
import sample.counter.api.CounterNotification;

import sample.counter.impl.CounterCommand.*;

public class CounterServiceImpl implements CounterService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final PubSubRef<CounterNotification> topic;

    @Inject
    public CounterServiceImpl(
                PersistentEntityRegistry persistentEntityRegistry, 
                PubSubRegistry pubSub) {

        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(CounterEntity.class);

        this.topic = pubSub.refFor(TopicId.of(CounterNotification.class, ""));
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

    @Override
    public ServiceCall<NotUsed, Source<CounterNotification, ?>> stream() {
        return req -> CompletableFuture.completedFuture(topic.subscriber());
    }

    private PersistentEntityRef<CounterCommand> getEntity(String id) {
        return persistentEntityRegistry.refFor(CounterEntity.class, id);
    }
}
