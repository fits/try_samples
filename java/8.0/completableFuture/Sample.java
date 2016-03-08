
import java.util.concurrent.CompletableFuture;

public class Sample {
	public static void main(String... args) {

		CompletableFuture<String> a = CompletableFuture.supplyAsync( () -> {
			try {
				Thread.sleep(5000);
				return "a1";
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});

		a.thenCombineAsync(
			CompletableFuture.completedFuture("b2"), 
			(x, y) -> x + " : " + y
		)
		.thenAcceptAsync(System.out::println)
		.join();
	}
}