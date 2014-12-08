import sodium.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.*;

class ReadLineFile {
	public static void main(String... args) throws Exception {
		Function<Integer, Function<Event<String>, Event<String>>> skip = n -> {
			return ev -> batch( v -> v >= n, n, ev);
		};

		Function<Integer, Function<Event<String>, Event<String>>> take = n -> {
			return ev -> batch( v -> v < n, n, ev);
		};

		Function<Event<String>, Event<String>> skipAndTake3 = skip.apply(1).andThen( take.apply(3) );

		EventSink<String> es = new EventSink<>();

		Listener esl = skipAndTake3.apply(es).map( v -> "# " + v ).listen( System.out::println );

		readFileLines(args[0], es);

		esl.unlisten();
	}

	private static void readFileLines(String fileName, EventSink<String> es) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			br.lines().forEach( es::send );
		}
	}

	private static Event<String> batch(Lambda1<Integer, Boolean> cond, int n, Event<String> ev) {
		BehaviorSink<Integer> counter = new BehaviorSink<>(0);

		ev.listen( v -> counter.send(counter.sample() + 1) );

		return ev.gate(counter.map(cond));
	}

/*
	private static Event<String> batch(Lambda1<Integer, Boolean> cond, int n, Event<String> ev) {
		BehaviorSink<Integer> counter = new BehaviorSink<>(0);
		final ArrayList<Listener> list = new ArrayList<>(1);

		list.add(ev.listen( v -> {
			int newValue = counter.sample() + 1;
			counter.send(newValue);

			if (newValue >= n) {
				list.stream().forEach( li -> li.unlisten() );
			}
		}));

		return ev.gate(counter.map(cond));
	}
*/
}