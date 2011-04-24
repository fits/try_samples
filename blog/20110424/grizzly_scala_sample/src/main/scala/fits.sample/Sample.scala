package fits.sample

import scala.io.Source
import org.glassfish.grizzly.http.server._

object Sample {
	def handler(proc: (Request, Response) => Unit) = {
		new HttpHandler {
			override def service(req: Request, res: Response) = {
				proc(req, res)
			}
		}
	}

	def main(args: Array[String]) {

		val server = HttpServer.createSimpleServer
		val conf = server.getServerConfiguration

		conf.addHttpHandler(
			handler { (req, res) => 
				res.getWriter.write("hello")
			}, "/test"
		)

		server.start

		println("Press key to stop")
		Source.stdin.reader().read()

		server.stop
	}
}
