package fits.sample

import dispatch._
import XhtmlParsing._

object Sample {

	def main(args: Array[String]) {
		if (args.length < 1) {
			return
		}

		val req = url("http://www.google.com/ig/api?weather=" + args(0) + "&hl=ja")

		println(req as_str)

		Http(req </> { nodes =>
			nodes \\ "current_conditions" \ "condition" \ "@data" foreach println
		})

	}
}
