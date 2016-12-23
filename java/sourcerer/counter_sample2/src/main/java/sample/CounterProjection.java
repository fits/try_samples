package sample;

import org.elder.sourcerer.AggregateProjection;
import org.jetbrains.annotations.NotNull;

public class CounterProjection implements AggregateProjection<Integer, CounterEvent>{

    @NotNull
    @Override
    public Integer empty() {
        return 0;
    }

    @NotNull
    @Override
    public Integer apply(@NotNull String id, @NotNull Integer state, @NotNull CounterEvent event) {
        int value = 0;

        if (event instanceof AddedCounter) {
            value = ((AddedCounter)event).getCount();
        }

        return state + value;
    }
}
