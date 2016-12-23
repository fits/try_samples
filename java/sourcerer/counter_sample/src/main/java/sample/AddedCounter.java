package sample;

import lombok.Data;
import org.elder.sourcerer.EventType;

@EventType
@Data
public class AddedCounter {
    private int count;
}
