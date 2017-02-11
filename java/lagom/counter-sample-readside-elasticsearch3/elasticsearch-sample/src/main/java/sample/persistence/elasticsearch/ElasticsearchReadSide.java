package sample.persistence.elasticsearch;

import akka.japi.pf.PFBuilder;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;

import java.util.concurrent.CompletionStage;
import java.util.function.UnaryOperator;

public interface ElasticsearchReadSide {

    <T extends AggregateEvent<T>> ReadSideHandlerBuilder<T> builder(String readSideId);

    interface ReadSideHandlerBuilder<T extends AggregateEvent<T>> {

        ReadSideHandlerBuilder<T> addHandler(
                UnaryOperator<PFBuilder<T, CompletionStage<?>>> handlerAppend);

        ReadSideProcessor.ReadSideHandler<T> build();
    }
}
