package sample.counter.monitor.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import sample.counter.api.CounterNotify;
import sample.counter.api.CounterService;
import sample.counter.monitor.CounterMonitorService;

import javax.inject.Inject;

public class CounterMonitorServiceImpl implements CounterMonitorService {
    @Inject
    public CounterMonitorServiceImpl(CounterService counterService) {
        counterService.counterTopic()
                .subscribe()
                .atLeastOnce(Flow.fromFunction(this::handleMessage));
    }

    private Done handleMessage(CounterNotify msg) {
        System.out.println("*** CounterMonitor handleMessage: " + msg);
        return Done.getInstance();
    }
}
