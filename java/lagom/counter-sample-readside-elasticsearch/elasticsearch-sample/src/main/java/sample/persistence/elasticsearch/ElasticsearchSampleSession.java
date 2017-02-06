package sample.persistence.elasticsearch;

import org.elasticsearch.client.Client;

import java.util.concurrent.*;
import java.util.function.Function;

public interface ElasticsearchSampleSession {

    <T> CompletionStage<T> withClientCompletable(Function<Client, CompletionStage<T>> block);

    default <T> CompletionStage<T> withClient(Function<Client, Future<T>> block) {
        return withClientCompletable(client -> toCompletableFuture(block.apply(client)));
    }

    default <T> CompletableFuture<T> toCompletableFuture(Future<T> future) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new CompletionException(e);
            }
        });
    }
}
