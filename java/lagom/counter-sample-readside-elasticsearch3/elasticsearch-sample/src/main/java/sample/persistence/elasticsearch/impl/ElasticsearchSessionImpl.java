package sample.persistence.elasticsearch.impl;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import com.typesafe.config.Config;
import play.inject.ApplicationLifecycle;

import java.net.URI;
import java.util.concurrent.CompletionStage;
import javax.inject.Singleton;

import sample.persistence.elasticsearch.ElasticsearchMessage;
import sample.persistence.elasticsearch.ElasticsearchSession;
import sample.persistence.elasticsearch.ElasticsearchUpdateMessage;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static sample.persistence.elasticsearch.impl.ElasticsearchConstants.*;

@Singleton
public class ElasticsearchSessionImpl implements ElasticsearchSession {
    private static final String SERVICE_NAME = "elasticsearch";
    private final ElasticsearchService esService;

    @Inject
    public ElasticsearchSessionImpl(ActorSystem system, ApplicationLifecycle lifecycle) {
        Config config = system.settings().config().getConfig(CONF_BASE);
        URI esUri = URI.create(config.getString(CONF_URL));

        esService = ClientFactoryUtil.createClient(lifecycle, SERVICE_NAME,
                ElasticsearchService.class, esUri);
    }

    @Override
    public CompletionStage<ElasticsearchMessage<?>> updateDoc(String index, String type, String id,
                                                              ElasticsearchUpdateMessage<?> doc) {

        return esService.updateDoc(index, type, id).invoke(doc);
    }

    public interface ElasticsearchService extends Service {
        ServiceCall<ElasticsearchUpdateMessage<?>, ElasticsearchMessage<?>> updateDoc(
                String index, String type, String id);

        @Override
        default Descriptor descriptor() {
            return named(SERVICE_NAME).withCalls(
                    restCall(Method.POST, "/:index/:type/:id/_update", this::updateDoc)
            );
        }
    }
}
