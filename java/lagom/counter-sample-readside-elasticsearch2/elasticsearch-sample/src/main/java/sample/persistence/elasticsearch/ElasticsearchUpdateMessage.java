package sample.persistence.elasticsearch;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class ElasticsearchUpdateMessage<T> implements Jsonable {
    private T doc;
    private boolean doc_as_upsert = true;
}
