package sample.persistence.elasticsearch.impl;

import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory;
import play.inject.ApplicationLifecycle;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class ClientFactoryUtil {

    public static <T> T createClient(ApplicationLifecycle lifecycle, String serviceName, Class<T> cls, URI uri) {
        LagomClientFactory factory = LagomClientFactory.create(serviceName, cls.getClassLoader());

        T res = factory.createClient(cls, uri);

        lifecycle.addStopHook(() -> {
            factory.close();
            return CompletableFuture.completedFuture(null);
        });

        return res;
    }
}
