package fits.sample

import scalaz._
import Scalaz._

object Sample2 extends App {

	val plus: Int => Int = 3 +
	val times: Int => Int = 2 * 

	val a0 = plus <*> times
	// (8, 10)
	println(a0(5))

	val a1 = ^(plus <*> times){ (a) => a._1 + a._2 }
	// 18
	println(a1(5))

	val a2 = plus <*> times >>> { (a) => a._1 + a._2 }
	// 18
	println(a2(5))
}
