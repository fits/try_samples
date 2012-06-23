package fits.sample

import unfiltered.request._
import unfiltered.response._

object SampleServer {

	def main(args: Array[String]) {
		val sample = unfiltered.filter.Planify {
			case _ => ResponseString("sample")
		}

		unfiltered.jetty.Http.local(8080).filter(sample).run()
	}
}
