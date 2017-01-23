package sample.counter.impl;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

public interface CounterEvent extends Jsonable {
	@Value
	public class CreatedCounter implements CounterEvent {
		private String name;
	}

	@Value
	public class IncrementalUpdatedCounter implements CounterEvent {
		private int diff;
	}

}
