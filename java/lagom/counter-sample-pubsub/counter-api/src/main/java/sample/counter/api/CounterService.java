package sample.counter.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

public interface CounterService extends Service {

    ServiceCall<NotUsed, Counter> create(String id);
    ServiceCall<NotUsed, Counter> find(String id);
    ServiceCall<CountMessage, Counter> update(String id);

    ServiceCall<NotUsed, Source<CounterNotification, ?>> stream();

    @Override
    default Descriptor descriptor() {
        return named("counter").withCalls(
                restCall(Method.POST, "/counter/:id", this::create),
                restCall(Method.GET, "/counter/:id", this::find),
                restCall(Method.PUT, "/counter/:id", this::update),
                restCall(Method.GET, "/stream", this::stream)
        ).withAutoAcl(true);
    }
}
