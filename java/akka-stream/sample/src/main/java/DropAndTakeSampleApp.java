
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.Source;

import scala.runtime.BoxedUnit;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class DropAndTakeSampleApp {
	public static void main(String... args) {
		final ActorSystem system = ActorSystem.create("sample");
		final FlowMaterializer materializer = FlowMaterializer.create(system);

		final OnComplete<BoxedUnit> complete = new OnComplete<BoxedUnit>() {
			@Override
			public void onComplete(Throwable failure, BoxedUnit success) {
				system.shutdown();
			}
		};

		List<String> data = IntStream.range(1, 7).mapToObj(i -> "sample" + i).collect(Collectors.toList());

		Source.from(data)
			.drop(3)
			.take(2)
			.map(s -> "#" + s)
			.foreach(System.out::println, materializer)
			.onComplete(complete, system.dispatcher());
	}
}