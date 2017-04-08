package sample.locator;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import play.Configuration;
import play.Environment;

public class CustomServiceLocatorModule extends AbstractModule {
    private final Environment env;
    private final Configuration conf;

    public CustomServiceLocatorModule(Environment env, Configuration conf) {
        this.env = env;
        this.conf = conf;
    }

    @Override
    protected void configure() {
        if (env.isProd()) {
            bind(ServiceLocator.class).to(CustomConfigurationServiceLocator.class);
        }
    }
}
