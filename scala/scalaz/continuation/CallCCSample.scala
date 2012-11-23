package fits.sample

import scalaz._
import Scalaz._

object CallCCSample extends App {
	def sample[R](n: Int): Continuation[R, Int] = Continuation.callCC { cc1: (Int => Continuation[R, Int]) =>
		if(n % 2 == 0) {
			cc1(n)
		}
		else {
			Continuation.continuationInstance[R].point(0)
		}
	}

	sample(1).runCont { println }
	sample(2).runCont { println }
	sample(3).runCont { println }
	sample(4).runCont { println }
}
