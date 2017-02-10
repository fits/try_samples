package sample.persistence.elasticsearch;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class OffsetMessage implements Jsonable {
    private String offset;
}
