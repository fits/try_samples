package fits.sample

import scalaz._
import Scalaz._

object ApplicativeSample extends App {
	val multiply = (x: Int) => (y: Int) => x * y
	val plus = (x: Int) => (y: Int) => x + y

	val f1 = multiply(2) <*> plus(10) >>> { case (a, b) => a + b }
	println(f1(4))

	val f1a = ^( multiply(2) <*> plus(10) ) { case (a, b) => a + b }
	println(f1a(4))

	val f1b = ( multiply(2) |@| plus(10) ) { _ + _ }
	println(f1b(4))


	val f2 = multiply(2) <*> plus(10) <*> plus(5) >>> { case ((a, b), c) => a + b + c }
	println(f2(4))

	val f2a = ^( multiply(2) <*> plus(10) <*> plus(5) ) { case ((a, b), c) => a + b + c }
	println(f2a(4))

	val f2b = ( multiply(2) |@| plus(10) |@| plus(5) ) { _ + _ + _ }
	println(f2b(4))
}
