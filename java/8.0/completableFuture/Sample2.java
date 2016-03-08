
import java.util.concurrent.CompletableFuture;

public class Sample2 {
	public static void main(String... args) {

		CompletableFuture<String> a = CompletableFuture.supplyAsync( () -> {
			try {
				Thread.sleep(5000);
				return "a";
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});

		CompletableFuture<String> b = CompletableFuture.completedFuture("b");

		CompletableFuture<String> c = a.thenCombineAsync(b, (x, y) -> x + "----" + y);

		c.thenAcceptAsync(System.out::println);

		CompletableFuture<Void> d = a.thenCombineAsync(
			CompletableFuture.supplyAsync( () -> {
				try {
					Thread.sleep(3000);
				} catch(InterruptedException e) {
				}
				throw new RuntimeException();
			}),
			(v1, v2) -> v1 + "-" + v2
		)
		.thenAcceptAsync(System.out::println) // not print
		.whenComplete( (v, e) -> {
			System.out.println("v=" + v + ", error=" + e);
		});

		c.join();

		try {
			d.join();
		} catch(Exception ex) {
			System.out.println(ex);
		}
	}
}