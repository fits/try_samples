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

    ServiceCall<InventoryCheckInParameter, Done> checkIn(String id);

    ServiceCall<NotUsed, InventoryItem> state(String id);

    @Override
    default Descriptor descriptor() {
        return named("inventory").withCalls(
            pathCall("/inventory/:id",  this::create),
            pathCall("/inventory/:id/checkin", this::checkIn),
            pathCall("/inventory/:id", this::state)
        ).withAutoAcl(true);
    }
}
