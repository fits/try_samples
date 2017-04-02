package sample.counter.proxy.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import sample.counter.api.Counter;

public interface CounterProxyService extends Service {

    ServiceCall<NotUsed, Counter> findCounter(String id);

    @Override
    default Descriptor descriptor() {
        return named("proxy").withCalls(
                restCall(Method.GET, "/proxy/:id", this::findCounter)
        ).withAutoAcl(true);
    }
}
