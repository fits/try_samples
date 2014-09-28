import sodium.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.*;

class ReadLineFile {
	public static void main(String... args) {
		Function<Integer, Function<Event<String>, Event<String>>> skip = n -> {
			return ev -> batch( v -> v <= 0, n, ev);
		};

		Function<Integer, Function<Event<String>, Event<String>>> take = n -> {
			return ev -> batch( v -> v > 0, n, ev);
		};

		Function<Event<String>, Event<String>> skipAndTake3 = skip.apply(1).andThen( take.apply(3) );

		BehaviorSink<String> bh = new BehaviorSink<>(null);

		Listener li = skipAndTake3.apply(bh.updates()).map( v -> "# " + v ).listen( System.out::println );

		try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {

			br.lines().forEach( line -> bh.send(line)  );

		} catch (IOException ex) {
		}

		li.unlisten();
	}

	private static Event<String> batch(Lambda1<Integer, Boolean> cond, int n, Event<String> ev) {
		BehaviorSink<Integer> counter = new BehaviorSink<>(n);
		final Listener[] li = new Listener[1];

		li[0] = ev.listen( v -> {
			int newValue = counter.sample() - 1;
			counter.send(newValue);

			if (newValue <= 0 && li[0] != null) {
				li[0].unlisten();
			}
		});

		return ev.gate(counter.map(cond));
	}
}