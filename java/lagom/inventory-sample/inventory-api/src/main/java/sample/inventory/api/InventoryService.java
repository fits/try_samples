package sample.inventory.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

public interface InventoryService extends Service {

    ServiceCall<InventoryCreateParameter, Done> create(String id);

    ServiceCall<NotUsed, Done> addStock(String id, int count);

    ServiceCall<NotUsed, InventoryItem> state(String id);

    @Override
    default Descriptor descriptor() {
        return named("inventory").withCalls(
            pathCall("/api/inv/:id",  this::create),
            pathCall("/api/inv/:id/add/:count", this::addStock),
            pathCall("/api/inv/:id/state", this::state)
        ).withAutoAcl(true);
    }
}
