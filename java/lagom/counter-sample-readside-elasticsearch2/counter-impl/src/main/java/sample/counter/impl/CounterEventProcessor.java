package sample.counter.impl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory;
import com.lightbend.lagom.javadsl.persistence.*;
import com.typesafe.config.Config;
import play.inject.ApplicationLifecycle;
import org.pcollections.ConsPStack;
import org.pcollections.PSequence;

import lombok.Value;
import lombok.val;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;

import sample.counter.impl.CounterEvent.*;
import sample.persistence.elasticsearch.ElasticsearchService;
import sample.persistence.elasticsearch.ElasticsearchUpdateMessage;
import sample.persistence.elasticsearch.OffsetMessage;

@Singleton
public class CounterEventProcessor extends ReadSideProcessor<CounterEvent> {
    private static final String SERVICE_NAME = "counter";
    private static final String CONF_ELASTICSEARCH = "sample.elasticsearch";
    private static final String CONF_URL = "url";

    private final ElasticsearchService esService;
    private final Config config;

    @Inject
    public CounterEventProcessor(ActorSystem system, ApplicationLifecycle lifecycle) {
        System.out.println("*** new CounterEventProcessor");

        config = system.settings().config().getConfig(CONF_ELASTICSEARCH);

        val esUri = URI.create(config.getString(CONF_URL));
        val factory = LagomClientFactory.create(SERVICE_NAME, getClass().getClassLoader());

        esService = factory.createClient(ElasticsearchService.class, esUri);

        lifecycle.addStopHook(() -> {
            factory.close();
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public ReadSideHandler<CounterEvent> buildHandler() {
        return new CounterEventHandler(esService, config);
    }

    @Override
    public PSequence<AggregateEventTag<CounterEvent>> aggregateTags() {
        return ConsPStack.singleton(CounterEvent.TAG);
    }

    private class CounterEventHandler extends ReadSideHandler<CounterEvent> {
        private static final String CONF_OFFSET_INDEX = "offset.index";
        private static final String CONF_OFFSET_TYPE = "offset.type";
        private static final String CONF_DATA_INDEX = "data.index";
        private static final String CONF_DATA_TYPE = "data.type";

        private final ElasticsearchService esService;
        private final String offsetIndex;
        private final String offsetType;
        private final String dataIndex;
        private final String dataType;

        private CounterEventHandler(ElasticsearchService esService, Config config) {
            this.esService = esService;

            offsetIndex = config.getString(CONF_OFFSET_INDEX);
            offsetType = config.getString(CONF_OFFSET_TYPE);
            dataIndex = config.getString(CONF_DATA_INDEX);
            dataType = config.getString(CONF_DATA_TYPE);
        }

        @Override
        public Flow<Pair<CounterEvent, Offset>, Done, ?> handle() {
            return Flow.<Pair<CounterEvent, Offset>>create().mapAsync(1, this::handleEvent);
        }

        @Override
        public CompletionStage<Offset> prepare(AggregateEventTag<CounterEvent> tag) {
            return esService.findOffset(offsetIndex, offsetType, SERVICE_NAME)
                    .invoke()
                    .thenApply(r -> r.get_source().getOffset())
                    .thenApply(this::toOffset)
                    .exceptionally(e -> Offset.NONE);
        }

        private CompletionStage<Done> handleEvent(Pair<CounterEvent, Offset> eventData) {
            val event = eventData.first();
            val offset = eventData.second();

            val countData = new CountData(count(event));

            return esService.updateDoc(dataIndex, dataType, event.getId())
                    .invoke(new ElasticsearchUpdateMessage<>(countData))
                    .thenCompose(r ->
                            esService.updateOffset(offsetIndex, offsetType, SERVICE_NAME)
                                    .invoke(new OffsetMessage(offset.toString()))
                    )
                    .thenApply(r -> Done.getInstance());
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

        @Value
        private class CountData {
            private int count;
        }
    }
}
