package sample.counter.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;
import sample.counter.api.Counter;

public interface CounterCommand extends Jsonable {
    @Value
    public class UpdateCounter implements CounterCommand, PersistentEntity.ReplyType<Counter> {
        private int count;
    }

    @Value
    public class CurrentCounter implements CounterCommand, PersistentEntity.ReplyType<Counter> {
    }
}
