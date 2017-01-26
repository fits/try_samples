package sample.counter.impl;

import akka.Done;
import akka.japi.Pair;
import akka.stream.javadsl.Flow;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import org.pcollections.ConsPStack;
import org.pcollections.PSequence;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CounterEventProcessor extends ReadSideProcessor<CounterEvent> {
	@Override
	public ReadSideHandler<CounterEvent> buildHandler() {
		return new CounterEventHandler();
	}

	@Override
	public PSequence<AggregateEventTag<CounterEvent>> aggregateTags() {
		return ConsPStack.singleton(CounterEvent.TAG);
	}

	private class CounterEventHandler extends ReadSideHandler<CounterEvent> {
		@Override
		public Flow<Pair<CounterEvent, Offset>, Done, ?> handle() {
			return Flow.<Pair<CounterEvent, Offset>>create().mapAsync(1, this::handleEvent);
		}

		private CompletionStage<Done> handleEvent(Pair<CounterEvent, Offset> event) {
			System.out.println("*** handle event: " + event);

			return CompletableFuture.completedFuture(Done.getInstance());
		}
	}
}
