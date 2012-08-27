package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	val r0 = Apply[Option].ap(some(10))(some((_: Int) + 1))
	println(r0)


}
