package sample.persistence.elasticsearch;

import java.util.concurrent.CompletionStage;

public interface ElasticsearchSession {

    CompletionStage<ElasticsearchMessage<?>> updateDoc(
            String index, String type, String id, ElasticsearchUpdateMessage<?> doc);
}
