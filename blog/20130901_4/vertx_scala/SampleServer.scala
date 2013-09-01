
import org.vertx.scala.core.Future
import org.vertx.scala.core.http.HttpServerRequest
import org.vertx.scala.platform.Verticle

/**
 * Scala による Vert.x の実行
 *
 *   vertx run scala:SampleServer
 */
class SampleServer extends Verticle {
	override def start(future: Future[Void]): Unit = {
		start()

		vertx.newHttpServer.requestHandler { req: HttpServerRequest =>
			req.response.end("test data")
		}.listen(8080)
	}
}

