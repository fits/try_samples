package sample.counter.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import lombok.Value;
import sample.counter.api.Counter;

public interface CounterCommand extends Jsonable {
	@Value
	public class CounterCreate implements CounterCommand, PersistentEntity.ReplyType<Counter> {
		private String name;
	}

	@Value
	public class CounterAdd implements CounterCommand, PersistentEntity.ReplyType<Counter> {
		private int incrementalCount;
	}

	@Value
	public class CurrentState implements CounterCommand, PersistentEntity.ReplyType<Counter> {
	}
}
