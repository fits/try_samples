package fits.sample;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import java.util.concurrent.CountDownLatch;

public class EmbeddedVertx {
	public static void main(String... args) throws Exception {

		Vertx vertx = VertxFactory.newVertx();

		vertx.createHttpServer().requestHandler( req ->
			req.response().end("test data")
		).listen(8080);

		System.out.println("started ...");

		waitStop();
	}

	private static void waitStop() {
		final CountDownLatch stopLatch = new CountDownLatch(1);

		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			System.out.println("shutdown ...");
			stopLatch.countDown();
		}));

		try {
			stopLatch.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
