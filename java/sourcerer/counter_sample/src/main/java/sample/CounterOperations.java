package sample;

import lombok.val;

public class CounterOperations {

    public static AddedCounter add(CounterAdd cmd) {
        val event = new AddedCounter();
        event.setCount(cmd.getCount());

        return event;
    }
}
