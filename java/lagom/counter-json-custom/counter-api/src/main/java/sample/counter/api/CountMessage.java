package sample.counter.api;

import lombok.Value;

@Value
public class CountMessage {
    private CounterId id;
    private int count;
}
