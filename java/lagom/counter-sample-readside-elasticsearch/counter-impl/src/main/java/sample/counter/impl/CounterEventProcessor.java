package sample.counter.impl;

import akka.Done;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.persistence.*;
import lombok.val;
import org.pcollections.ConsPStack;
import org.pcollections.PSequence;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;

import sample.persistence.elasticsearch.ElasticsearchSampleSession;
import sample.counter.impl.CounterEvent.*;

public class CounterEventProcessor extends ReadSideProcessor<CounterEvent> {
    private final ElasticsearchSampleSession elsSession;

    @Inject
    public CounterEventProcessor(ElasticsearchSampleSession elsSession) {
        this.elsSession = elsSession;

        System.out.println("*** new CounterEventProcessor:" + elsSession);
    }

    @Override
    public ReadSideHandler<CounterEvent> buildHandler() {
        return new CounterEventHandler(elsSession);
    }

    @Override
    public PSequence<AggregateEventTag<CounterEvent>> aggregateTags() {
        return ConsPStack.singleton(CounterEvent.TAG);
    }

    private class CounterEventHandler extends ReadSideHandler<CounterEvent> {
        private static final String COUNTER_INDEX = "counter";
        private static final String COUNTER_META_TYPE = "meta";
        private static final String COUNTER_DATA_TYPE = "data";
        private static final String COUNTER_OFFSET_ID = "common";
        private static final String OFFSET_FIELD = "offset";
        private static final String COUNT_FIELD = "count";

        private final ElasticsearchSampleSession elsSession;

        CounterEventHandler(ElasticsearchSampleSession elsSession) {
            this.elsSession = elsSession;
        }

        @Override
        public Flow<Pair<CounterEvent, Offset>, Done, ?> handle() {
            return Flow.<Pair<CounterEvent, Offset>>create().mapAsync(1, this::handleEvent);
        }

        @Override
        public CompletionStage<Offset> prepare(AggregateEventTag<CounterEvent> tag) {
            return elsSession
                    .withClient(client ->
                            client.prepareGet(COUNTER_INDEX, COUNTER_META_TYPE, COUNTER_OFFSET_ID)
                                    .execute()
                    )
                    .thenApply(r -> r.getSource().get(OFFSET_FIELD).toString())
                    .thenApply(this::toOffset)
                    .exceptionally(e -> Offset.NONE);
        }

        private CompletionStage<Done> handleEvent(Pair<CounterEvent, Offset> event) {
            return elsSession.withClient(client -> {
                CounterEvent ev = event.first();

                val updateData = client.prepareIndex(COUNTER_INDEX, COUNTER_DATA_TYPE)
                        .setId(ev.getId())
                        .setSource(COUNT_FIELD, String.valueOf(count(ev)));

                val saveOffset = client.prepareIndex(COUNTER_INDEX, COUNTER_META_TYPE)
                        .setId(COUNTER_OFFSET_ID)
                        .setSource(OFFSET_FIELD, event.second().toString());

                return client.prepareBulk()
                        .add(updateData)
                        .add(saveOffset)
                        .execute();

            }).thenApply(r -> Done.getInstance());
        }

        private int count(CounterEvent event) {
            if (event instanceof CounterUpdated) {
                return ((CounterUpdated)event).getCount();
            }
            return 0;
        }

        private Offset toOffset(String str) {
            if (str.contains("-")) {
                return Offset.timeBasedUUID(UUID.fromString(str));
            }
            else {
                return Offset.sequence(Long.parseLong(str));
            }
        }
    }
}
