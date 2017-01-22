package sample.counter.impl;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;
import lombok.Value;
import sample.counter.api.CounterState;

public interface CounterCommand extends Jsonable {
	@Value
	public class CounterCreate implements CounterCommand, PersistentEntity.ReplyType<CounterState> {
		private String name;
	}

	@Value
	public class CounterAdd implements CounterCommand, PersistentEntity.ReplyType<CounterState> {
		private int incrementalCount;
	}

	@Value
	public class CurrentState implements CounterCommand, PersistentEntity.ReplyType<CounterState> {
	}
}
