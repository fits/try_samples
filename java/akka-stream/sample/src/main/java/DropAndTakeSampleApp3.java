
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.FlowMaterializer;
import akka.stream.javadsl.Source;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DropAndTakeSampleApp3 {
	public static void main(String... args) {
		final ActorSystem system = ActorSystem.create("sample");
		final FlowMaterializer materializer = FlowMaterializer.create(system);

		final OnComplete<BoxedUnit> complete = new OnComplete<BoxedUnit>() {
			@Override
			public void onComplete(Throwable failure, BoxedUnit success) {
				system.shutdown();
			}
		};

		try (SamplePublisher<String> publisher = new SamplePublisher<>()) {

			Source.from(publisher)
				.drop(3)
				.take(2)
				.map(s -> "#" + s)
				.foreach(System.out::println, materializer)
				.onComplete(complete, system.dispatcher());

			IntStream.range(1, 6)
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