package fits.sample

import dispatch._
import XhtmlParsing._

object Sample {

	def main(args: Array[String]) {
		val req = url("http://www.google.com/ig/api?weather=" + args(0) + "&hl=ja")

		println(req as_str)

		Http(req </> { nodes =>
			val weather = nodes \\ "current_conditions" \ "condition" \ "@data"
			println(weather)
		})

	}
}
