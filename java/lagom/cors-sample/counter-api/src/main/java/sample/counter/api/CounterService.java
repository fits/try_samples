package sample.counter.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceAcl;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

public interface CounterService extends Service {

    ServiceCall<NotUsed, Counter> find(String id);
    ServiceCall<CountMessage, Counter> update(String id);

    @Override
    default Descriptor descriptor() {
        return named("counter").withCalls(
                restCall(Method.GET, "/counter/:id", this::find),
                restCall(Method.POST, "/counter/:id", this::update)
        )
        .withAutoAcl(true)
        .withServiceAcls(
            ServiceAcl.methodAndPath(Method.OPTIONS, "/counter/.*")
        );
    }
}
