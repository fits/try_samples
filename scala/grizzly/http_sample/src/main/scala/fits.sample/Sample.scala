package fits.sample

import scala.io.Source
import org.glassfish.grizzly.http.server._

object Sample {
	def main(args: Array[String]) {

		val server = HttpServer.createSimpleServer

		server.getServerConfiguration.addHttpHandler(
			new HttpHandler {
				override def service(req: Request, res: Response) = {
					res.getWriter.write("http server sample")
				}
			}, 
			"/test"
		)

		server.start

		println("Press key to stop")
		Source.stdin.reader().read()

		server.stop
	}
}
