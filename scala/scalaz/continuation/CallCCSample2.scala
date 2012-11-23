package fits.sample

import scalaz._
import Scalaz._

object CallCCSample2 extends App {
	import Continuation._

	type CtInt[R] = Continuation[R, Int]

	def sample[R](n: Int): CtInt[R] = callCC { cc1: (Int => CtInt[R]) =>

		if(n % 2 == 1) {
			cc1(n)
		}
		else {
			for {
				x <- callCC { cc2: (Int => CtInt[R]) => 
					if (n < 4) {
						cc2(n * 1000)
					}
					else if (n == 4) {
						cc1(n * 100)
					}
					else {
						continuationInstance[R].point(n * 10)
					}
				}
			} yield (x + 1)
		}
	}

	sample(1).runCont { println }
	sample(2).runCont { println }
	sample(3).runCont { println }
	sample(4).runCont { println }
	sample(5).runCont { println }
	sample(6).runCont { println }
}
