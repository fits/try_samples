package sample.counter.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

public interface CounterEvent extends CompressedJsonable, AggregateEvent<CounterEvent> {

    AggregateEventTag<CounterEvent> TAG = AggregateEventTag.of(CounterEvent.class);

    @Override
    default AggregateEventTag<CounterEvent> aggregateTag() {
        return TAG;
    }

    @Value
    public class CounterCreated implements CounterEvent {
        private String id;
    }

    @Value
    public class CounterUpdated implements CounterEvent {
        private int count;
    }
}
