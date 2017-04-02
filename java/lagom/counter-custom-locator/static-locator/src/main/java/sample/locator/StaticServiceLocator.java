package sample.locator;

import akka.actor.ActorSystem;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.ServiceLocator;

import lombok.val;

import javax.inject.Inject;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class StaticServiceLocator implements ServiceLocator {
    private static final String CONF_STATIC_URL = "sample.static.url";
    private final URI staticUrl;

    @Inject
    public StaticServiceLocator(ActorSystem system) {
        val config = system.settings().config();
        staticUrl = URI.create(config.getString(CONF_STATIC_URL));
    }

    @Override
    public CompletionStage<Optional<URI>> locate(String s, Descriptor.Call<?, ?> call) {
        return CompletableFuture.completedFuture(Optional.of(staticUrl));
    }

    @Override
    public <T> CompletionStage<Optional<T>> doWithService(String s, Descriptor.Call<?, ?> call, Function<URI, CompletionStage<T>> function) {
        return locate(s, call).thenCompose(u ->
                u.map(uri -> function.apply(uri).thenApply(Optional::ofNullable))
                        .orElse(CompletableFuture.completedFuture(Optional.empty()))
        );
    }
}
