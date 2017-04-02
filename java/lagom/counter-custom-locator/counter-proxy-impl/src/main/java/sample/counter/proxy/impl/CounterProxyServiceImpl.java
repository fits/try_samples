package sample.counter.proxy.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import javax.inject.Inject;

import sample.counter.api.Counter;
import sample.counter.api.CounterService;
import sample.counter.proxy.api.CounterProxyService;

public class CounterProxyServiceImpl implements CounterProxyService {
    private final CounterService counterService;

    @Inject
    public CounterProxyServiceImpl(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public ServiceCall<NotUsed, Counter> findCounter(String id) {
        return req -> counterService.find(id).invoke();
    }
}
