package sample;

import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Streams;
import reactor.rx.stream.Broadcaster;

public class StreamSample {
	public static void main(String... args) throws Exception {
		Environment env = new Environment();

		Broadcaster<String> stream = Streams.<String>broadcast(env);

		Promise<String> promise = stream.observe(System.out::println).next();

		Streams.range(1, 5).consume( i -> stream.onNext("sample" + i) );

		promise.await();

		env.shutdown();
	}
}
