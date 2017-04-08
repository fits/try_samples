package sample.counter.api;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class Counter implements Jsonable {
    private String id;
    private int count;
}
