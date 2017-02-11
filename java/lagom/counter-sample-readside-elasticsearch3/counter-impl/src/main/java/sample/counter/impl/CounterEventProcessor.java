package sample.counter.impl;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.*;
import com.typesafe.config.Config;
import org.pcollections.ConsPStack;
import org.pcollections.PSequence;

import lombok.Value;

import java.util.concurrent.CompletionStage;

import sample.persistence.elasticsearch.ElasticsearchReadSide;
import sample.persistence.elasticsearch.ElasticsearchSession;
import sample.persistence.elasticsearch.ElasticsearchUpdateMessage;
import sample.counter.impl.CounterEvent.*;

@Singleton
public class CounterEventProcessor extends ReadSideProcessor<CounterEvent> {
    private static final String SERVICE_NAME = "counter";
    private static final String CONF_READSIDE = "sample.readside";
    private static final String CONF_INDEX = "index";
    private static final String CONF_TYPE = "type";

    private final ElasticsearchSession esSession;
    private final ElasticsearchReadSide esReadSide;
    private final String esIndex;
    private final String esType;

    @Inject
    public CounterEventProcessor(ActorSystem system,
                                 ElasticsearchSession esSession, ElasticsearchReadSide esReadSide) {

        System.out.println("*** new CounterEventProcessor");

        this.esSession = esSession;
        this.esReadSide = esReadSide;

        Config config = system.settings().config().getConfig(CONF_READSIDE);

        esIndex = config.getString(CONF_INDEX);
        esType = config.getString(CONF_TYPE);
    }

    @Override
    public ReadSideHandler<CounterEvent> buildHandler() {
        return esReadSide.<CounterEvent>builder(SERVICE_NAME)
                .addHandler(pfb -> pfb.match(CounterCreated.class, this::updateDoc))
                .addHandler(pfb -> pfb.match(CounterUpdated.class, this::updateDoc))
                .build();
    }

    @Override
    public PSequence<AggregateEventTag<CounterEvent>> aggregateTags() {
        return ConsPStack.singleton(CounterEvent.TAG);
    }


    private CompletionStage<?> updateDoc(CounterCreated event) {
        return updateDoc(event.getId(), 0);
    }

    private CompletionStage<?> updateDoc(CounterUpdated event) {
        return updateDoc(event.getId(), event.getCount());
    }

    private CompletionStage<?> updateDoc(String id, int count) {
        return esSession.updateDoc(esIndex, esType, id,
                new ElasticsearchUpdateMessage<>(new CounterData(count)));
    }

    @Value
    private class CounterData {
        private int count;
    }
}
