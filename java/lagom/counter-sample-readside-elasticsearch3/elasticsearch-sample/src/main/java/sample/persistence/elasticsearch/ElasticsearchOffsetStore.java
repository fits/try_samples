package sample.persistence.elasticsearch;

import com.lightbend.lagom.javadsl.persistence.Offset;
import java.util.concurrent.CompletionStage;

public interface ElasticsearchOffsetStore {

    CompletionStage<ElasticsearchMessage<?>> updateOffset(String id, String tag, Offset offset);

    CompletionStage<ElasticsearchMessage<OffsetMessage>> findOffset(String id, String tag);
}
