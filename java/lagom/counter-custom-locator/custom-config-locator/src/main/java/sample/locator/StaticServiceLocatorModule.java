package sample.locator;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import play.Configuration;
import play.Environment;

public class StaticServiceLocatorModule extends AbstractModule {
    private final Environment env;
    private final Configuration conf;

    public StaticServiceLocatorModule(Environment env, Configuration conf) {
        this.env = env;
        this.conf = conf;
    }

    @Override
    protected void configure() {
        if (env.isProd()) {
            bind(ServiceLocator.class).to(StaticServiceLocator.class);
        }
    }
}
