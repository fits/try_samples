package sample.persistence.elasticsearch.impl;

import akka.NotUsed;
import akka.actor.ActorSystem;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.typesafe.config.Config;
import play.inject.ApplicationLifecycle;

import java.net.URI;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.inject.Singleton;

import sample.persistence.elasticsearch.ElasticsearchMessage;
import sample.persistence.elasticsearch.ElasticsearchOffsetStore;
import sample.persistence.elasticsearch.OffsetMessage;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static sample.persistence.elasticsearch.impl.ElasticsearchConstants.*;

@Singleton
public class ElasticsearchOffsetStoreImpl implements ElasticsearchOffsetStore {
    private static final String SERVICE_NAME = "elasticsearchOffset";

    private final ElasticsearchOffsetService offsetService;
    private final String offsetIndex;

    @Inject
    public ElasticsearchOffsetStoreImpl(ActorSystem system, ApplicationLifecycle lifecycle) {
        Config config = system.settings().config().getConfig(CONF_BASE);
        URI esUri = URI.create(config.getString(CONF_URL));

        offsetService = ClientFactoryUtil.createClient(lifecycle, SERVICE_NAME,
                ElasticsearchOffsetService.class, esUri);

        offsetIndex = config.getString(CONF_OFFSET_INDEX);
    }

    @Override
    public CompletionStage<ElasticsearchMessage<?>> updateOffset(String id, String tag, Offset offset) {
        return offsetService.updateOffset(offsetIndex, tag, id)
                .invoke(new OffsetMessage(offset.toString()));
    }

    @Override
    public CompletionStage<ElasticsearchMessage<OffsetMessage>> findOffset(String id, String tag) {
        return offsetService.findOffset(offsetIndex, tag, id).invoke();
    }

    public interface ElasticsearchOffsetService extends Service {
        ServiceCall<OffsetMessage, ElasticsearchMessage<?>> updateOffset(String index, String type, String id);
        ServiceCall<NotUsed, ElasticsearchMessage<OffsetMessage>> findOffset(String index, String type, String id);

        @Override
        default Descriptor descriptor() {
            return named(SERVICE_NAME).withCalls(
                    restCall(Method.POST, "/:index/:type/:id", this::updateOffset),
                    restCall(Method.GET, "/:index/:type/:id", this::findOffset)
            );
        }
    }
}
