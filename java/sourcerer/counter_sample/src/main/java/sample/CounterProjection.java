package sample;

import org.elder.sourcerer.AggregateProjection;
import org.jetbrains.annotations.NotNull;

public class CounterProjection implements AggregateProjection<Integer, AddedCounter>{

    @NotNull
    @Override
    public Integer empty() {
        return 0;
    }

    @NotNull
    @Override
    public Integer apply(@NotNull String id, @NotNull Integer state, @NotNull AddedCounter event) {
        return state + event.getCount();
    }
}
