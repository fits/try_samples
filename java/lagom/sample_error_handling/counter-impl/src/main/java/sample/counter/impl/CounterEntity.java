package sample.counter.impl;

import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import sample.counter.api.CounterState;

import sample.counter.impl.CounterCommand.*;
import sample.counter.impl.CounterEvent.*;

public class CounterEntity extends PersistentEntity<CounterCommand, CounterEvent, CounterState> {

	@Override
	public Behavior initialBehavior(Optional<CounterState> snapshotState) {
		BehaviorBuilder builder = newBehaviorBuilder(
				snapshotState.orElse(new CounterState(entityId(), "", 0, false))
		);

		builder.setCommandHandler(CounterCreate.class, (cmd, ctx) ->
				ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(state())));

		builder.setCommandHandler(CounterAdd.class, (cmd, ctx) ->
				ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(state())));

		builder.setReadOnlyCommandHandler(CurrentState.class, (cmd, ctx) -> ctx.reply(state()));

		builder.setEventHandler(CreatedCounter.class, this::createState);
		builder.setEventHandler(IncrementalUpdatedCounter.class, this::updateState);

		return builder.build();
	}

	private CreatedCounter toEvent(CounterCreate cmd) {
		return new CreatedCounter(cmd.getName());
	}

	private IncrementalUpdatedCounter toEvent(CounterAdd cmd) {
		return new IncrementalUpdatedCounter(cmd.getIncrementalCount());
	}

	private CounterState createState(CreatedCounter event) {
		return new CounterState(entityId(), event.getName(), 0, true);
	}

	private CounterState updateState(IncrementalUpdatedCounter event) {
		return new CounterState(entityId(), state().getName(),
				state().getCount() + event.getDiff(), state().isActive());
	}
}
