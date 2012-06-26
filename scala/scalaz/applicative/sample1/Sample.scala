package fits.sample

import scalaz._
import Scalaz._

object Sample {

	def main(args: Array[String]) {
		val r = (List(1, 2, 3, 4) |@| List(10, 20)) {_ + _}
		println(r)
	}
}
