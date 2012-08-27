package fits.sample

import scalaz._
import Scalaz._

object MonoidSample extends App {

	println(mzero[Int])

	val i1 = 1 mappend 2
	println(i1)

	val i2 = 5 |+| 12
	println(i2)

	val i3 = some(100) |+| some(-18)
	println(i3)

	val i4 = 2 |+| 3 |+| 5 |+| 1
	println(i4)

	val i5 = mzero[Int] |+| 2 |+| 3 |+| 5 |+| 1
	println(i5)


	println(mzero[String])

	val s1 = "test" mappend "!!"
	println(s1)

	val s2 = some("!") |+| some("abc") |+| mzero[Option[String]]
	println(s2)


}
