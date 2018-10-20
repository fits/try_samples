package sample;

import io.lettuce.core.RedisClient;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.util.UUID;

public class SampleApp {
	public static void main(String... args) {
		var redisClient = RedisClient.create("redis://localhost/");
		var connection = redisClient.connect();
		var cmd = connection.reactive();

		var id = UUID.randomUUID().toString();
		var qty = 10;

		var res = createStock(cmd, id, qty)
				.log()
				.flatMap(i -> reserve(cmd, id, random(qty)))
				.log()
				.flatMap(r -> reserve(cmd, id, random(qty)))
				.log();

		System.out.println(res.blockOptional());
	}

	private static int random(int qty) {
		int res = (int)(Math.random() * qty);
		return Math.max(res, 1);
	}

	private static Mono<Reservation> reserve(RedisReactiveCommands<String, String> cmd,
								String id, int reserveQty) {

		System.out.println("*** reserveQty: " + reserveQty);

		var key = stockKey(id);
		var rsvKey = reservationKey(id);

		var rsvId = UUID.randomUUID().toString();

		return cmd.watch(rsvKey)
				.filter(s -> s.equals("OK"))
				.flatMap(s -> cmd.llen(rsvKey))
				.flatMap(r ->
						cmd.get(key)
								.map(q -> new Stock(Integer.parseInt(q), r.intValue()))
				)
				.log()
				.filter(stock -> stock.getAvailableQuantity() >= reserveQty)
				.flatMap(stock -> {
					MonoProcessor<TransactionResult> processor = MonoProcessor.create();

					cmd.multi().subscribe(r -> {
						cmd.lpush(rsvKey, repeat(rsvId, reserveQty)).subscribe();
						cmd.exec().subscribe(processor);
					});

					return processor;
				})
				.filter(t -> t.size() == 1)
				.map(t -> new Reservation(rsvId, reserveQty))
		;
	}

	private static String[] repeat(String s, int size) {
		String[] res = new String[size];

		for (int i = 0; i < res.length; i++) {
			res[i] = s;
		}

		return res;
	}

	private static Mono<String> createStock(RedisReactiveCommands<String, String> cmd,
											String id, int quantity) {

		var key = stockKey(id);

		return cmd.setnx(key, Integer.toString(quantity))
				.filter(s -> s)
				.map(s -> id);
	}

	private static String stockKey(String id) {
		return "stock-" + id;
	}

	private static String reservationKey(String id) {
		return "reservation-" + id;
	}

	static class Stock {
		private final int quantity;
		private final int reserved;

		Stock(int quantity, int reserved) {
			this.quantity = quantity;
			this.reserved = reserved;
		}

		int getAvailableQuantity() {
			return quantity - reserved;
		}

		public String toString() {
			return "Stock( quantity: " + quantity + ", reserved: " + reserved + " )";
		}
	}

	static class Reservation {
		private final String rsvId;
		private final int quantity;

		Reservation(String rsvId, int quantity) {
			this.rsvId = rsvId;
			this.quantity = quantity;
		}

		public String toString() {
			return "Reservation( rsvId: " + rsvId + ", quantity: " + quantity + " )";
		}
	}
}
