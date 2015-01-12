package sample;

import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.Source;
import akka.stream.OverflowStrategy;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ProducerSample1 {
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

		try (SamplePublisher<String> publisher = new SamplePublisher<>()) {

			Source.from(publisher)
				// Input buffer overrun
				.buffer(10, OverflowStrategy.backpressure())
				.map(s -> "#" + s)
				.foreach(System.out::println, materializer)
				.onComplete(complete, system.dispatcher());

			IntStream.range(1, 7)
				.mapToObj(i -> "sample" + i)
				.forEach(d -> publisher.send(d));
		}
	}

	private static class SamplePublisher<T> implements Publisher<T>, AutoCloseable {
		private List<Subscriber<? super T>> list = new ArrayList<>();

		@Override
		public void subscribe(Subscriber<? super T> s) {
			list.add(s);

			s.onSubscribe(new Subscription() {
				@Override public void request(long n) {}
				@Override public void cancel() {}
			});
		}

		@Override
		public void close() {
			list.forEach(Subscriber::onComplete);
		}

		public void send(T msg) {
			list.forEach(s -> s.onNext(msg));
		}
	}
}