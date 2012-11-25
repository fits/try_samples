package fits.sample

import scalaz._
import Scalaz._

object CallCCSample extends App {
	import Cont._

	def sample[R](n: Int): Cont[R, Int] = callCC { cc1: (Int => Cont[R, Int]) =>
		if(n % 2 == 1) {
			cc1(n) // (1)
		}
		else {
			contInstance[R].point(n * 10) // (2)
		}
	}

	sample(1).runCont { println } // (1)
	sample(2).runCont { println } // (2)
	sample(3).runCont { println } // (1)
	sample(4).runCont { println } // (2)
}
