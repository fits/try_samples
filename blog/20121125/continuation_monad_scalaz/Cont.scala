package fits.sample

import scalaz._
import Scalaz._

case class Cont[R, A](runCont: (A => R) => R)

trait ContMonad[R] extends Monad[({type r[a] = Cont[R, a]})#r] {

	def point[A](a: => A) = Cont { k => k(a) }

	def bind[A, B](fa: Cont[R, A])(f: (A) => Cont[R, B]) = {
		Cont { k =>
			fa.runCont { a =>
				f(a).runCont(k)
			}
		}
	}
}

trait ContFunctions {
	def callCC[R, A, B](f: (A => Cont[R, B]) => Cont[R, A]): Cont[R, A] = {
		Cont { k =>
			f { a =>
				Cont { _ => k(a) }
			}.runCont(k)
		}
	}
}

trait ContInstances {
	implicit def contInstance[R] = new ContMonad[R] {
	}
}

case object Cont extends ContFunctions with ContInstances
