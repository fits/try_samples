package fits.sample

import scala.io.Source
import org.glassfish.grizzly.http.server._

object Handler {
	def apply(proc: (Request, Response) => Unit) = {
		new HttpHandler {
			override def service(req: Request, res: Response) = {
				proc(req, res)
			}
		}
	}
}

object Sample {
	def main(args: Array[String]) {

		val server = HttpServer.createSimpleServer
		val conf = server.getServerConfiguration

		conf.addHttpHandler(
			Handler { (req, res) => 
				res.getWriter.write("http server sample")
			}, "/test"
		)

		server.start

		println("Press key to stop")
		Source.stdin.reader().read()

		server.stop
	}
}
