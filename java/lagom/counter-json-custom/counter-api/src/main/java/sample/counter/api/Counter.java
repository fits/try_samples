package sample.counter.api;

import lombok.Value;

@Value
public class Counter {
    private CounterId id;
    private int count;
}
