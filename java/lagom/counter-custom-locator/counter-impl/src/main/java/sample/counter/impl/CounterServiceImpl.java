package sample.counter.impl;

import akka.NotUsed;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import play.api.inject.ApplicationLifecycle;
import sample.counter.api.CountMessage;
import sample.counter.api.Counter;
import sample.counter.api.CounterService;
import sample.counter.impl.CounterCommand.*;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class CounterServiceImpl implements CounterService {
    private final PersistentEntityRegistry entityRegistry;

    @Inject
    public CounterServiceImpl(PersistentEntityRegistry entityRegistry,
                              ApplicationLifecycle lifecycle) {

        this.entityRegistry = entityRegistry;
        entityRegistry.register(CounterEntity.class);

        lifecycle.addStopHook(() ->
                entityRegistry.gracefulShutdown(Duration.create(5, TimeUnit.SECONDS)));
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
        return entityRegistry.refFor(CounterEntity.class, id);
    }
}
