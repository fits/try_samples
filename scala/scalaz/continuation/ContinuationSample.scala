package fits.sample

import scalaz._
import Scalaz._

object ContinuationSample extends App {
	def calc1[R](x: Int) = Continuation.continuationInstance[R].point(x + 3)

	def calc2[R](x: Int) = Continuation.continuationInstance[R].point(x * 10)

	def calcAll[R](x: Int) = Continuation.continuationInstance[R].point(x) >>= calc1 >>= calc2

	calc1(2).runCont { println }

	calcAll(2).runCont { println }

	calcAll(2).runCont { x => x - 9 } |> println
}
