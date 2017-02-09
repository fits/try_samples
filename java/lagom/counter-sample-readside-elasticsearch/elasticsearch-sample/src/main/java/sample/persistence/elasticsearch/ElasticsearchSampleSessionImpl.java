package sample.persistence.elasticsearch;

import javax.inject.Singleton;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import play.inject.ApplicationLifecycle;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class ElasticsearchSampleSessionImpl implements ElasticsearchSampleSession {
    private static final String CONF_ELASTICSEARCH = "sample.elasticsearch";
    private final Client elsClient;

    @Inject
    public ElasticsearchSampleSessionImpl(ActorSystem system, ApplicationLifecycle lifecycle) {
        String hostAndPort = system.settings().config().getString(CONF_ELASTICSEARCH);

        elsClient = createClient(parseTransportAddress(hostAndPort));

        lifecycle.addStopHook(() -> {
            elsClient.close();
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public <T> CompletionStage<T> withClientCompletable(Function<Client, CompletionStage<T>> block) {
        return block.apply(elsClient);
    }

    private InetSocketTransportAddress parseTransportAddress(String hostAndPort) {
        String[] elms = hostAndPort.split(":");

        try {
            return new InetSocketTransportAddress(
					InetAddress.getByName(elms[0]), Integer.parseInt(elms[1]));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private Client createClient(InetSocketTransportAddress address) {
        return new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(address);
    }
}
