package fits.sample

import scalaz._
import Scalaz._

object CallCCSample extends App {

	def sample[F[+_]](n: Int): Codensity[F, Int] = CodensityFunc.callCC { cc1: (Int => Codensity[F, Int]) =>
		if (n % 2 == 1) {
			cc1(n)
		}
		else {
			Codensity.pureCodensity(0)
		}
	}

	sample(1).apply { Option(_) } foreach(println)
	sample(2).apply { Option(_) } foreach(println)
	sample(3).apply { Option(_) } foreach(println)
	sample(4).apply { Option(_) } foreach(println)
	sample(5).apply { Option(_) } foreach(println)
	sample(6).apply { Option(_) } foreach(println)
}
