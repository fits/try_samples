import sodium.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.*;

class ReadLineFile2 {
	public static void main(String... args) throws Exception {
		Function<Integer, Function<Event<String>, Event<String>>> skip = n -> {
			return ev -> batch( v -> v >= n, n, ev);
		};

		Function<Integer, Function<Event<String>, Event<String>>> take = n -> {
			return ev -> batch( v -> v < n, n, ev);
		};

		Function<Event<String>, Event<String>> skipAndTake = skip.apply(1).andThen( take.apply(5) ).andThen( skip.apply(2) );

		EventSink<String> es = new EventSink<>();

		Listener esl = skipAndTake.apply(es).map( v -> "# " + v ).listen( System.out::println );

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
}