package fits.sample

import scalaz._
import Scalaz._

object ContinuationSample extends App {
	import Continuation._

	def cont[R](a: Int) = continuationInstance[R].point(a)

	def calc1[R](x: Int) = cont[R](x + 3)
	def calc2[R](x: Int) = cont[R](x * 10)

	def calcAll[R](x: Int) = cont[R](x) >>= calc1 >>= calc2


	calc1(2).runCont { println } // 5

	calcAll(2).runCont { println } // 50

	calcAll(2).runCont { x => x - 9 } |> println // 41
}
