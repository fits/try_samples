package fits.sample;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

public class Sample {
	public static void main(String... args) throws Exception {

		Vertx vertx = VertxFactory.newVertx();

		vertx.createHttpServer().requestHandler( req ->
			req.response().end("test data")
		).listen(8080);

		System.in.read();
	}
}
