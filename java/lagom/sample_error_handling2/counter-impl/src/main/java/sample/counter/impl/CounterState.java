package sample.counter.impl;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;
import sample.counter.api.Counter;

@Value
public class CounterState implements Jsonable {
	private String id;
	private String name;
	@Wither @NonFinal private int count;
	private boolean active;

	public Counter toCounter() {
		return new Counter(id, name, count);
	}
}
