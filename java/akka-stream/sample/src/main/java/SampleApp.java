
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.Source;

import scala.runtime.BoxedUnit;

import java.util.Arrays;

public class SampleApp {
	public static void main(String... args) {
		final ActorSystem system = ActorSystem.create("sample");
		final FlowMaterializer materializer = FlowMaterializer.create(system);

		final OnComplete<BoxedUnit> complete = new OnComplete<BoxedUnit>() {
			@Override
			public void onComplete(Throwable failure, BoxedUnit success) {
				system.shutdown();
			}
		};

		Source.from(Arrays.asList("test", "sample", "testData", "123"))
			.map(String::toUpperCase)
			.foreach(System.out::println, materializer)
			.onComplete(complete, system.dispatcher());

	}
}