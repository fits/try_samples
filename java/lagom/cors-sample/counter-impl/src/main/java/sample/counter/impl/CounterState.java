package sample.counter.impl;

import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;
import sample.counter.api.Counter;

@Value
public final class CounterState implements CompressedJsonable {
    @Wither @NonFinal private String id;
    @Wither @NonFinal private int count;

    public Counter toCounter() {
        return new Counter(id, count);
    }

    public static CounterState empty() {
        return new CounterState("", 0);
    }
}
