package sample;

import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.stream.Broadcaster;
import reactor.rx.action.support.TapAndControls;

import java.util.concurrent.atomic.AtomicInteger;

public class Sample2 {
	public static void main(String... args) throws Exception {
		Environment env = new Environment();

		skipAndTakeSampleA(env);

		System.out.println("-----");

		skipAndTakeSampleB(env);

		env.shutdown();
	}

	// #sampleA-3
	private static void skipAndTakeSampleA(Environment env) throws Exception {
		Broadcaster<String> stream = Streams.broadcast(env);

		Promise<String> promise = skip(stream, 2)
			.take(3)
			.map(s -> "#" + s)
			.observe(System.out::println)
			.next();

		Streams.range(1, 6).consume( i -> stream.onNext("sampleA-" + i) );

		promise.await();
	}

	// #sampleB-3
	// #sampleB-4
	// #sampleB-5
	private static void skipAndTakeSampleB(Environment env) throws Exception {
		Broadcaster<String> stream = Streams.broadcast(env);

		TapAndControls<String> tap = skip(stream, 2)
			.take(3)
			.map(s -> "#" + s)
			.observe(System.out::println)
			.tap();

		Streams.range(1, 6).consume( i -> stream.onNext("sampleB-" + i) );

		tap.get();
	}

	private static <T> Stream<T> skip(Stream<T> st, int num) {
		AtomicInteger counter = new AtomicInteger();

		return st.filter(s -> counter.incrementAndGet() > num);
	}
}
