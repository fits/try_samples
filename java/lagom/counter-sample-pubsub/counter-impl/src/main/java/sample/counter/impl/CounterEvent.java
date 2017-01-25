package sample.counter.impl;

import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

import sample.counter.api.CounterNotification;

public interface CounterEvent extends CompressedJsonable {
    @Value
    public class CounterCreated implements CounterEvent, CounterNotification {
        private String id;
    }

    @Value
    public class CounterUpdated implements CounterEvent {
        private int count;
    }
}
