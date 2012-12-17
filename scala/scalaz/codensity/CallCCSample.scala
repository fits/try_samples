package fits.sample

import scalaz._
import Scalaz._

object CallCCSample extends App {

	def callCC[F[+_], A, B](f: (A => Codensity[F, B]) => Codensity[F, A]): Codensity[F, A] = {
		new Codensity[F, A] { 
			def apply[C](k: A => F[C]) = {
				f { a: A =>
					new Codensity[F, B] {
						override def apply[D](f: B => F[D]) = {
							k(a).asInstanceOf[F[D]]
						}
					}
				}.apply(k)
			}
		}
	}

	def sample[F[+_]](n: Int): Codensity[F, Int] = callCC { cc1: (Int => Codensity[F, Int]) =>
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
