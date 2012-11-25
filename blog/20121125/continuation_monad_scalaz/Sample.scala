package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {
	import Cont._

	def cont[R](a: Int) = contInstance[R].point(a)

	def calc1[R](x: Int) = cont[R](x + 3)

	def calc2[R](x: Int) = cont[R](x * 10)

	def calc3[R](x: Int) = cont[R](x + 4)

	def calcAll[R](x: Int) = cont[R](x) >>= calc1 >>= calc2 >>= calc3


	calc1(2).runCont { println } // 2 + 3 = 5

	calcAll(2).runCont { println } // ((2 + 3) * 10) + 4 = 54

	calcAll(2).runCont { x => x - 9 } |> println // 54 - 9 = 45
}
