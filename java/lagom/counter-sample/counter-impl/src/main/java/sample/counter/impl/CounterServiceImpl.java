package sample.counter.impl;

import akka.NotUsed;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import sample.counter.api.CountMessage;
import sample.counter.api.Counter;
import sample.counter.api.CounterService;
import sample.counter.impl.CounterCommand.*;

public class CounterServiceImpl implements CounterService {
    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    public CounterServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {

        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(CounterEntity.class);
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

    private PersistentEntityRef<CounterCommand> getEntity(String id) {
        return persistentEntityRegistry.refFor(CounterEntity.class, id);
    }
}
