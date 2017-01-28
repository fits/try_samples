package sample.counter.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import com.google.inject.Inject;
import sample.counter.api.CounterNotify;
import sample.counter.api.CounterService;

public class CounterMonitor {
	@Inject
	public CounterMonitor(CounterService counterService) {
		counterService.counterTopic()
				.subscribe()
				.atLeastOnce(Flow.fromFunction(this::handleMessage));
	}

	private Done handleMessage(CounterNotify msg) {
		System.out.println("*** handleMessage: " + msg);
		return Done.getInstance();
	}
}
