package sample.persistence.elasticsearch;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class ElasticsearchMessage<T> implements Jsonable {
    private String _id;
    private String _index;
    private String _type;
    private long _version;
    private String result;
    private boolean created;
    private T _source;

}
