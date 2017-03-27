package sample.counter.monitor;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import sample.counter.api.CounterService;
import sample.counter.monitor.impl.CounterMonitorServiceImpl;

public class CounterMonitorModule extends AbstractModule implements ServiceGuiceSupport {
	@Override
	protected void configure() {
		bindClient(CounterService.class);
		bindServices(serviceBinding(CounterMonitorService.class, CounterMonitorServiceImpl.class));
	}
}
