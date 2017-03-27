package sample.counter.impl;

import akka.NotUsed;

import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import sample.counter.api.CountMessage;
import sample.counter.api.Counter;
import sample.counter.api.CounterNotify;
import sample.counter.api.CounterService;
import sample.counter.impl.CounterCommand.*;

public class CounterServiceImpl implements CounterService {
    private final PersistentEntityRegistry entityRegistry;

    @Inject
    public CounterServiceImpl(PersistentEntityRegistry entityRegistry) {
        this.entityRegistry = entityRegistry;
        entityRegistry.register(CounterEntity.class);
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
    public Topic<CounterNotify> counterTopic() {
        return TopicProducer.singleStreamWithOffset(offset ->
                entityRegistry.eventStream(CounterEvent.TAG, offset)
                        .map(this::convert)
        );
    }

    private Pair<CounterNotify, Offset> convert(Pair<CounterEvent, Offset> data) {
        return Pair.create(
                new CounterNotify(data.first().toString()),
                data.second()
        );
    }

    private PersistentEntityRef<CounterCommand> getEntity(String id) {
        return entityRegistry.refFor(CounterEntity.class, id);
    }
}
