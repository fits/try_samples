package sample.counter.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

public interface CounterService extends Service {
	ServiceCall<CreateParameter, Counter> createCounter();
	ServiceCall<CounterParameter, Counter> countUp(String id);
	ServiceCall<NotUsed, Counter> findCounter(String id);

	@Override
	default Descriptor descriptor() {
		return named("counterservice").withCalls(
				restCall(Method.POST, "/counter", this::createCounter),
				restCall(Method.POST, "/counter/:id/up", this::countUp),
				restCall(Method.GET, "/counter/:id", this::findCounter)
		).withAutoAcl(true);
	}
}
