package fits.sample

import scalaz._
import Scalaz._

object CodensityFunc {

	def callCC[F[+_], A, B](f: (A => Codensity[F, B]) => Codensity[F, A]): Codensity[F, A] = {
		new Codensity[F, A] { 
			// (a)
			def apply[C](k: A => F[C]) = {
				f { a: A =>
					new Codensity[F, B] {
						/*
						 * 以下で apply[C](・・・) としても
						 * (a) の apply における C とは別の型として扱われる
						 */
						override def apply[D](f: B => F[D]) = {
							/*
							 * k(a) の結果は F[C] だが、
							 * この apply の結果を
							 * F[D] とする必要があるため
							 * C と D が同じ型である事を見越して
							 * asInstanceOf[F[D]] で回避している
							 */
							k(a).asInstanceOf[F[D]]
						}
					}
				}.apply(k)
			}
		}
	}
}
