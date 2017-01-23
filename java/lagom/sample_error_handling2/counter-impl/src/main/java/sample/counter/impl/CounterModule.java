package sample.counter.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sample.counter.api.CounterService;

public class CounterModule extends AbstractModule implements ServiceGuiceSupport {
	@Override
	protected void configure() {
		bindServices(serviceBinding(CounterService.class, CounterServiceImpl.class));
	}
}
