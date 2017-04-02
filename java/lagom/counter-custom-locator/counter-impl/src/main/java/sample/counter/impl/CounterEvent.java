package sample.counter.impl;

import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

public interface CounterEvent extends CompressedJsonable {
    @Value
    public class CounterUpdated implements CounterEvent {
        private int count;
    }
}
