package sample.counter.impl;

import java.util.Optional;

import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import sample.counter.api.Counter;
import sample.counter.impl.CounterCommand.*;
import sample.counter.impl.CounterEvent.*;

public class CounterEntity extends PersistentEntity<CounterCommand, CounterEvent, CounterState> {

    @Override
    public Behavior initialBehavior(Optional<CounterState> snapshotState) {
        BehaviorBuilder builder = newBehaviorBuilder(
                snapshotState.orElseGet(CounterState::empty)
        );

        builder.setCommandHandler(CreateCounter.class, (cmd, ctx) ->
            ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(currentCounter()))
        );

        builder.setCommandHandler(UpdateCounter.class, (cmd, ctx) -> {
            if (isNotCreated()) {
                ctx.commandFailed(counterNotFound());
                return ctx.done();
            }

            return ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(currentCounter()));
        });

        builder.setReadOnlyCommandHandler(CurrentCounter.class, (cmd, ctx) -> {
            if (isNotCreated()) {
                ctx.commandFailed(counterNotFound());
            }

            ctx.reply(currentCounter());
        });

        builder.setEventHandler(CounterCreated.class, this::toState);
        builder.setEventHandler(CounterUpdated.class, this::toState);

        return builder.build();
    }

    private boolean isNotCreated() {
        return state().getId() == null;
    }

    private NotFound counterNotFound() {
        return new NotFound("not exists counter: id=" + entityId());
    }

    private CounterCreated toEvent(CreateCounter cmd) {
        return new CounterCreated(entityId());
    }

    private CounterUpdated toEvent(UpdateCounter cmd) {
        return new CounterUpdated(cmd.getCount());
    }

    private CounterState toState(CounterCreated event) {
        return new CounterState(entityId(), 0);
    }

    private CounterState toState(CounterUpdated event) {
        return state().withCount(event.getCount());
    }

    private Counter currentCounter() {
        return state().toCounter();
    }
}
