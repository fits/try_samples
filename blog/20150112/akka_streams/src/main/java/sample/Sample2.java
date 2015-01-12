package sample;

import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import scala.runtime.BoxedUnit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sample2 {
	public static void main(String... args) {
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

		Flow<String, String> flow = Flow.<String>create()
			.drop(3)
			.take(2)
			.map(s -> "#" + s);

		List<String> data = IntStream.range(1, 7).mapToObj(i -> "sample" + i).collect(Collectors.toList());

		flow.runWith(
			Source.from(data), 
			Sink.foreach(System.out::println), 
			materializer
		).onComplete(complete, system.dispatcher());
	}
}