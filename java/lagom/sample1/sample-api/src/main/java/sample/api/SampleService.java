package sample.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

public interface SampleService extends Service {

	ServiceCall<NotUsed, String> helloGet(String id);
	ServiceCall<String, String> helloPost(String id);

	@Override
	default Descriptor descriptor() {
		return named("sample").withCalls(
			pathCall("/api/sample/:id",  this::helloGet),
			pathCall("/api/sample/:id",  this::helloPost)
		).withAutoAcl(true);
	}
}
