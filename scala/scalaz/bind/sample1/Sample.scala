package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	val r0 = some(3) >>= {x => some("abc" + x)} >>= {_ => none}
	println(r0)

	val r1 = some(3) >>= {x => some("abc" + x)}
	println(r1)

	val r2 = ^(some(3)) {"abc" + _}
	println(r2)

	val r3 = List(1, 2, 3) >>= {x => List(x + 10)}
	println(r3)

	val r4 = ^(List(1, 2, 3)) { _ + 10 }
	println(r4)

}
