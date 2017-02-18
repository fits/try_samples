package sample.counter.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.api.ConfigurationServiceLocator;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import play.Configuration;
import play.Environment;
import sample.counter.api.CounterService;

public class CounterModule extends AbstractModule implements ServiceGuiceSupport {
    private final Environment env;
    private final Configuration conf;

    public CounterModule(Environment env, Configuration conf) {
        this.env = env;
        this.conf = conf;
    }

    @Override
    protected void configure() {
        bindServices(serviceBinding(CounterService.class, CounterServiceImpl.class));

        if (env.isProd()) {
            bind(ServiceLocator.class).to(ConfigurationServiceLocator.class);
        }
    }
}
