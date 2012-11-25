package fits.sample

import scalaz._
import Scalaz._

object CallCCSample2a extends App {
	import Cont._

	val odd = (n: Int) => n % 2 == 1

	def when[M[_], A](cond: Boolean)(f: => M[A])(implicit M: Pointed[M]) = 
		if (cond) f else M.point(())

	def sample[R](n: Int): Cont[R, Int] = callCC { cc1: (Int => Cont[R, Int]) =>
		for {
			_ <- when (odd(n)) {
				for {
					_ <- cc1(n) // (1)
				} yield ()
			}
			x <- callCC { cc2: (Int => Cont[R, Int]) =>
				for {
					_ <- when (n < 4) {
						for {
							_ <- cc2(n * 1000) // (2)
						} yield ()
					}
					_ <- when (n == 4) {
						for {
							_ <- cc1(n * 100) // (3)
						} yield ()
					}
				} yield (n * 10) // (4)
			}
		} yield (x + 1) // (5)
	}

	sample(1).runCont { println } // (1)
	sample(2).runCont { println } // (2) (5)
	sample(3).runCont { println } // (1)
	sample(4).runCont { println } // (3)
	sample(5).runCont { println } // (1)
	sample(6).runCont { println } // (4) (5)
}
