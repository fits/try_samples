package sample;

import reactor.core.publisher.Mono;

import java.time.Duration;

public class SampleApp {
	public static void main(String... args) {
		doRetry();

		System.out.println("---");

		doRetryInterval();
	}

	private static void doRetry() {
		var res = Mono.just("one")
				.log()
				.flatMap(s -> Mono.just("two"))
				.log()
				.flatMap(s -> {
					throw new RuntimeException();
				})
				.log()
				.retry(2)
				;

		try {
			System.out.println(res.blockOptional());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private static void doRetryInterval() {
		var res = Mono.just("1st")
				.log()
				.flatMap(s -> Mono.just("2nd"))
				.log()
				.flatMap(s -> {
					throw new RuntimeException();
				})
				.log()
				.retryBackoff(2, Duration.ofSeconds(3))
				;

		try {
			System.out.println(res.blockOptional());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
