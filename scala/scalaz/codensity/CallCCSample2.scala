package fits.sample

import scalaz._
import Scalaz._

object CallCCSample2 extends App {

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
			cc1(n) // (1)
		}
		else {
			for {
				x <- callCC { cc2: (Int => Codensity[F, Int]) =>
					n match {
						case x if (x < 4) => cc2(n * 1000) // (2)
						case 4 => cc1(n * 100) // (3)
						case _ => Codensity.pureCodensity(n * 10): Codensity[F, Int] // (4)
					}
				}
			} yield (x + 1) // (5)
		}
	}

	sample(1).apply { Option(_) } foreach(println) // (1)
	sample(2).apply { Option(_) } foreach(println) // (2) (5)
	sample(3).apply { Option(_) } foreach(println) // (1)
	sample(4).apply { Option(_) } foreach(println) // (3)
	sample(5).apply { Option(_) } foreach(println) // (1)
	sample(6).apply { Option(_) } foreach(println) // (4) (5)
}
