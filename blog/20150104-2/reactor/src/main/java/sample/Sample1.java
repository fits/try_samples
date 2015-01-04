package sample;

import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Streams;
import reactor.rx.stream.Broadcaster;

public class Sample1 {
	public static void main(String... args) throws Exception {
		Environment env = new Environment();

		Broadcaster<String> stream = Streams.broadcast(env);

		Promise<String> promise = stream.observe(System.out::println).next();

		Streams.range(1, 6).consume( i -> stream.onNext("sample" + i) );

		promise.await();

		env.shutdown();
	}
}
