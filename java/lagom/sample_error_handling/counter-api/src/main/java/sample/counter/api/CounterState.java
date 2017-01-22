package sample.counter.api;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class CounterState implements Jsonable {
	private String id;
	private String name;
	private int count;
	private boolean active;
}
