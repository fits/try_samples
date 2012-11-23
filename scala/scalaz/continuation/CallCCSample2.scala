package fits.sample

import scalaz._
import Scalaz._

object CallCCSample2 extends App {
	import Continuation._

	type CtInt[R] = Continuation[R, Int]

	def sample[R](n: Int): CtInt[R] = callCC { cc1: (Int => CtInt[R]) =>
		if(n % 2 == 1) {
			cc1(n) // (1)
		}
		else {
			for {
				x <- callCC { cc2: (Int => CtInt[R]) =>
					n match {
						case x if (x < 4) => cc2(n * 1000) // (2)
						case 4 => cc1(n * 100) // (3)
						case _ => continuationInstance[R].point(n * 10) // (4)
					}
				}
			} yield (x + 1) // (5)
		}
	}

	sample(1).runCont { println } // (1)
	sample(2).runCont { println } // (2) (5)
	sample(3).runCont { println } // (1)
	sample(4).runCont { println } // (3)
	sample(5).runCont { println } // (1)
	sample(6).runCont { println } // (4) (5)
}
