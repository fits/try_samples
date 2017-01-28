package sample.counter.api;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class CounterNotify implements Jsonable {
	private String message;
}
