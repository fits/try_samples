package sample.counter.impl;

import java.util.Optional;

import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import sample.counter.api.Counter;
import sample.counter.impl.CounterCommand.*;
import sample.counter.impl.CounterEvent.*;

public class CounterEntity extends PersistentEntity<CounterCommand, CounterEvent, CounterState> {

	@Override
	public Behavior initialBehavior(Optional<CounterState> snapshotState) {
		BehaviorBuilder builder = newBehaviorBuilder(
				snapshotState.orElse(new CounterState(entityId(), "", 0, false))
		);

		builder.setCommandHandler(CounterCreate.class, (cmd, ctx) ->
				ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(currentCounter())));

		builder.setCommandHandler(CounterAdd.class, (cmd, ctx) -> {
			if (!state().isActive()) {
				ctx.commandFailed(notFound());
				return ctx.done();
			}
			return ctx.thenPersist(toEvent(cmd), ev -> ctx.reply(currentCounter()));
		});

		builder.setReadOnlyCommandHandler(CurrentState.class, (cmd, ctx) -> {
			if (!state().isActive()) {
				ctx.commandFailed(notFound());
			}
			ctx.reply(currentCounter());
		});

		builder.setEventHandler(CreatedCounter.class, this::createState);
		builder.setEventHandler(IncrementalUpdatedCounter.class, this::updateState);

		return builder.build();
	}

	private Counter currentCounter() {
		return state().toCounter();
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
		return state().withCount(state().getCount() + event.getDiff());
	}

	private NotFound notFound() {
		return new NotFound("not exists " + entityId());
	}
}
