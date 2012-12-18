package fits.sample

import scalaz._
import Scalaz._

object CodensityFunc {

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
}
