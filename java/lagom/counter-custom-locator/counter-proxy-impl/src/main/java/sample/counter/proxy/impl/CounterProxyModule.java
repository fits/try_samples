package sample.counter.proxy.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import sample.counter.api.CounterService;
import sample.counter.proxy.api.CounterProxyService;

public class CounterProxyModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(CounterProxyService.class, CounterProxyServiceImpl.class));
        bindClient(CounterService.class);
    }
}
