package fits.sample

import scalaz._
import Scalaz._

object Sample {

	def main(args: Array[String]) {
		val r = (List(1, 2, 3, 4) |@| List(10, 20)) {_ + _}
		println(r)

		val f = (x: Int) => x * 2
		val g = (y: Int) => y + 4

		val r2 = g compose f apply 5
		println(r2)


		val a1 = List(10, 20) |@| List(1, 5, 10) |@| List(2, 3)
		println(a1 {_ + _  + _})
	}
}
