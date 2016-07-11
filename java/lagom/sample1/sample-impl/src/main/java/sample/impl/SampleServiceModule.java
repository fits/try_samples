package sample.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import sample.api.SampleService;

public class SampleServiceModule extends AbstractModule 
	implements ServiceGuiceSupport {

	@Override
	protected void configure() {
		bindServices(
			serviceBinding(SampleService.class, SampleServiceImpl.class)
		);
	}
}
