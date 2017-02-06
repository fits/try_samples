package sample.persistence.elasticsearch;

import javax.inject.Singleton;
import com.google.inject.Inject;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import play.inject.ApplicationLifecycle;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class ElasticsearchSampleSessionImpl implements ElasticsearchSampleSession {
    private final Client elsClient;

    @Inject
    public ElasticsearchSampleSessionImpl(ApplicationLifecycle lifecycle) {
        elsClient = createClient();

        lifecycle.addStopHook(() -> {
            elsClient.close();
            return CompletableFuture.completedFuture(null);
        });
    }

    @Override
    public <T> CompletionStage<T> withClientCompletable(Function<Client, CompletionStage<T>> block) {
        return block.apply(elsClient);
    }


    private Client createClient() {
        // TODO: 設定ファイルから接続先を取得

        InetSocketTransportAddress addr = new InetSocketTransportAddress(
                InetAddress.getLoopbackAddress(), 9300);

        return new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(addr);
    }
}
