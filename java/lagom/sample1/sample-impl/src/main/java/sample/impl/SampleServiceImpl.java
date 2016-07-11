package sample.impl;

import static java.util.concurrent.CompletableFuture.completedFuture;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import sample.api.SampleService;

public class SampleServiceImpl implements SampleService {

	@Override
	public ServiceCall<NotUsed, String> helloGet(String id) {
		return req -> completedFuture("id: " + id + ", req: " + req);
	}

	@Override
	public ServiceCall<String, String> helloPost(String id) {
		return param -> completedFuture("id: " + id + ", param: " + param);
	}
}
