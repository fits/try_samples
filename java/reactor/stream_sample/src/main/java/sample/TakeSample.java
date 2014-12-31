package sample;

import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Streams;
import reactor.rx.stream.Broadcaster;
import reactor.rx.action.support.TapAndControls;

public class TakeSample {
	public static void main(String... args) throws Exception {
		Environment env = new Environment();

		takeSampleA(env);

		System.out.println("-----");

		takeSampleB(env);

		env.shutdown();
	}

	// sampleA-1
	private static void takeSampleA(Environment env) throws Exception {
		Broadcaster<String> stream = Streams.<String>broadcast(env);

		Promise<String> promise = stream.take(3).observe(System.out::println).next();

		Streams.range(1, 5).consume( i -> stream.onNext("sampleA-" + i) );

		promise.await();
	}

	// sampleB-1
	// sampleB-2
	// sampleB-3
	private static void takeSampleB(Environment env) {
		Broadcaster<String> stream = Streams.<String>broadcast(env);

		TapAndControls<String> tap = stream.take(3).observe(System.out::println).tap();

		Streams.range(1, 5).consume( i -> stream.onNext("sampleB-" + i) );

		tap.get();
	}
}
