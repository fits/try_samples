package sample.persistence.elasticsearch.impl;

import akka.Done;
import akka.japi.Pair;
import akka.japi.pf.PFBuilder;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.UnaryOperator;
import javax.inject.Inject;
import javax.inject.Singleton;

import scala.PartialFunction;

import sample.persistence.elasticsearch.ElasticsearchOffsetStore;
import sample.persistence.elasticsearch.ElasticsearchReadSide;

@Singleton
@Slf4j
public class ElasticsearchReadSideImpl implements ElasticsearchReadSide {
    private final ElasticsearchOffsetStore offsetStore;

    @Inject
    public ElasticsearchReadSideImpl(ElasticsearchOffsetStore offsetStore) {
        this.offsetStore = offsetStore;
    }

    @Override
    public <T extends AggregateEvent<T>> ReadSideHandlerBuilder<T> builder(String readSideId) {
        return new ReadSideHandlerBuilder<T>() {
            private PFBuilder<T, CompletionStage<?>> handlerBuilder = new PFBuilder<>();

            @Override
            public ReadSideHandlerBuilder<T> addHandler(
                    UnaryOperator<PFBuilder<T, CompletionStage<?>>> handlerAppend) {

                handlerBuilder = handlerAppend.apply(handlerBuilder);
                return this;
            }

            @Override
            public ReadSideProcessor.ReadSideHandler<T> build() {
                return new ElasticsearchEventHandler<>(readSideId, offsetStore,
                        handlerBuilder.build());
            }
        };
    }

    private class ElasticsearchEventHandler<T extends AggregateEvent<T>>
            extends ReadSideProcessor.ReadSideHandler<T> {

        private final String readSideId;
        private final ElasticsearchOffsetStore offsetStore;
        private final PartialFunction<T, CompletionStage<?>> eventHandlers;
        private volatile AggregateEventTag<T> tag;

        private ElasticsearchEventHandler(String readSideId,
                                         ElasticsearchOffsetStore offsetStore,
                                         PartialFunction<T, CompletionStage<?>> eventHandlers) {

            this.readSideId = readSideId;
            this.offsetStore = offsetStore;
            this.eventHandlers = eventHandlers;
        }

        @Override
        public Flow<Pair<T, Offset>, Done, ?> handle() {
            return Flow.<Pair<T, Offset>>create()
                    .mapAsync(1, this::handleEvent);
        }

        @Override
        public CompletionStage<Offset> prepare(AggregateEventTag<T> tag) {
            this.tag = tag;

            return offsetStore.findOffset(readSideId, tag.tag())
                    .thenApply(r -> r.get_source().getOffset())
                    .thenApply(this::toOffset)
                    .exceptionally(e -> {
                        log.debug("no offset data", e);
                        return Offset.NONE;
                    });
        }

        private CompletionStage<Done> handleEvent(Pair<T, Offset> eventData) {
            T event = eventData.first();

            if (!eventHandlers.isDefinedAt(event)) {
                log.warn("no event handler {}", event);
                return CompletableFuture.completedFuture(Done.getInstance());
            }

            return eventHandlers.apply(event)
                    .thenCompose(r ->
                            offsetStore.updateOffset(readSideId, tag.tag(), eventData.second()))
                    .thenApply(r -> Done.getInstance());
        }

        private Offset toOffset(String value) {
            if (value.contains("-")) {
                return Offset.timeBasedUUID(UUID.fromString(value));
            }
            else {
                return Offset.sequence(Long.parseLong(value));
            }
        }
    }
}
