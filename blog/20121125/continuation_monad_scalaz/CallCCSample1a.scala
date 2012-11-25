package fits.sample

import scalaz._
import Scalaz._

object CallCCSample1a extends App {
	import Cont._

	val odd = (n: Int) => n % 2 == 1

	def when[M[_], A](cond: Boolean)(f: => M[A])(implicit M: Pointed[M]) = 
		if (cond) f else M.point(())

	def sample[R](n: Int): Cont[R, Int] = callCC { cc: (Int => Cont[R, Int]) =>
		for {
			_ <- when (odd(n)) {
				for {
					_ <- cc(n) // (1)
				} yield ()
			}
		} yield (n * 10) // (2)
	}

	sample(1).runCont { println } // (1)
	sample(2).runCont { println } // (2)
	sample(3).runCont { println } // (1)
	sample(4).runCont { println } // (2)
}
