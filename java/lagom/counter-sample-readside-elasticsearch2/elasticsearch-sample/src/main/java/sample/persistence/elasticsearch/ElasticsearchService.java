package sample.persistence.elasticsearch;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.Service.named;

public interface ElasticsearchService extends Service {

    ServiceCall<OffsetMessage, ElasticsearchMessage<?>> updateOffset(String index, String type, String id);
    ServiceCall<NotUsed, ElasticsearchMessage<OffsetMessage>> findOffset(String index, String type, String id);

    ServiceCall<ElasticsearchUpdateMessage<?>, ElasticsearchMessage<?>> updateDoc(String index, String type, String id);

    @Override
    default Descriptor descriptor() {
        return named("elasticsearch").withCalls(
                restCall(Method.POST, "/:index/:type/:id", this::updateOffset),
                restCall(Method.GET, "/:index/:type/:id", this::findOffset),
                restCall(Method.POST, "/:index/:type/:id/_update", this::updateDoc)
        ).withAutoAcl(true);
    }
}
