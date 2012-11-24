package fits.sample

import scalaz._
import Scalaz._

object CallCCSample extends App {
	import Continuation._

	def sample[R](n: Int): Continuation[R, Int] = callCC { cc1: (Int => Continuation[R, Int]) =>
		if(n % 2 == 1) {
			cc1(n) // (1)
		}
		else {
			continuationInstance[R].point(0) // (2)
		}
	}

	sample(1).runCont { println } // (1)
	sample(2).runCont { println } // (2)
	sample(3).runCont { println } // (1)
	sample(4).runCont { println } // (2)
}
