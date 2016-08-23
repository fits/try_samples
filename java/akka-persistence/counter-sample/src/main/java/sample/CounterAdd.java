package sample;

import lombok.Value;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Value
public class CounterAdd implements Serializable {
    private int count;
}
