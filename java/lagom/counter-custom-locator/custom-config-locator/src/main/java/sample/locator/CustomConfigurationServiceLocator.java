package sample.locator;

import com.lightbend.lagom.internal.client.CircuitBreakers;
import com.lightbend.lagom.javadsl.api.ConfigurationServiceLocator;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.client.CircuitBreakingServiceLocator;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class CustomConfigurationServiceLocator extends CircuitBreakingServiceLocator {
    private static final String DEFAULT_SERVICE_NAME = "default";
    private final ConfigurationServiceLocator configLocator;

    @Inject
    public CustomConfigurationServiceLocator(Configuration configuration,
                                             CircuitBreakers circuitBreakers) {
        super(circuitBreakers);
        configLocator = new ConfigurationServiceLocator(configuration);
    }

    @Override
    public CompletionStage<Optional<URI>> locate(String s, Descriptor.Call<?, ?> call) {
        return configLocator.locate(s, call)
                .thenCompose(opt ->
                        opt.<CompletionStage<Optional<URI>>>map(u ->
                                CompletableFuture.completedFuture(opt)
                        ).orElseGet(this::locateDefault)
                );
    }

    private CompletionStage<Optional<URI>> locateDefault() {
        return configLocator.locate(DEFAULT_SERVICE_NAME);
    }
}
