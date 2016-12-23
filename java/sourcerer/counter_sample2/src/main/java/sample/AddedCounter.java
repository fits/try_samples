package sample;

import lombok.Data;

@Data
public class AddedCounter implements CounterEvent {
    private int count;
}
