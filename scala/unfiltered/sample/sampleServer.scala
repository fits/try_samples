package fits.sample

import unfiltered.request._
import unfiltered.response._

object SampleServer {

	def main(args: Array[String]) {
		val sample = unfiltered.filter.Planify {
			case GET(Path("/test.html")) => Html(
				<html>
				<body>
					<h1>test</h1>
				</body>
				</html>
			)

			case GET(Path(Seg("users" :: id :: Nil))) => ResponseString("id = " + id)
			case _ => ResponseString("sample")
		}

		unfiltered.jetty.Http.local(8080).filter(sample).run()
	}
}
