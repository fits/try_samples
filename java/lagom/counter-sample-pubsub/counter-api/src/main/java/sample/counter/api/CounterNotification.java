package sample.counter.api;

import com.lightbend.lagom.serialization.Jsonable;

public interface CounterNotification extends Jsonable {
    String getId();
}
