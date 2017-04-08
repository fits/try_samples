package sample.counter.impl;

import java.util.Optional;

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

        builder.setCommandHandler(UpdateCounter.class, (cmd, ctx) ->
                ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(currentCounter())));

        builder.setReadOnlyCommandHandler(CurrentCounter.class, (cmd, ctx) ->
                ctx.reply(currentCounter()));

        builder.setEventHandler(CounterUpdated.class, this::toState);

        return builder.build();
    }

    private CounterUpdated toEvent(UpdateCounter cmd) {
        return new CounterUpdated(cmd.getCount());
    }

    private CounterState toState(CounterUpdated event) {
        return state()
                .withId(entityId())
                .withCount(event.getCount());
    }

    private Counter currentCounter() {
        System.out.println("state: " + state());

        return state().toCounter();
    }
}
