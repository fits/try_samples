package sample;

import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.Source;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import scala.runtime.BoxedUnit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Sample1 {
	public static void main(String... args) {
		// disable log
		final Config config = ConfigFactory.load()
			.withValue("akka.loglevel", ConfigValueFactory.fromAnyRef("error"));

		final ActorSystem system = ActorSystem.create("sample", config);
		final FlowMaterializer materializer = FlowMaterializer.create(system);

		final OnComplete<BoxedUnit> complete = new OnComplete<BoxedUnit>() {
			@Override
			public void onComplete(Throwable failure, BoxedUnit success) {
				system.shutdown();
			}
		};

		// sample1 Å` sample6
		List<String> data = IntStream.range(1, 7).mapToObj(i -> "sample" + i).collect(Collectors.toList());

		//Iterable<String> data = () -> IntStream.range(1, 7).mapToObj(i -> "sample" + i).iterator();

		Source.from(data)
			.drop(3)
			.take(2)
			.map(s -> "#" + s)
			.foreach(System.out::println, materializer)
			.onComplete(complete, system.dispatcher());
	}
}