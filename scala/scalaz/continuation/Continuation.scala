package fits.sample

import scalaz._
import Scalaz._

case class Continuation[R, A](runCont: (A => R) => R)

trait ContinuationMonad[R] extends Monad[({type r[a] = Continuation[R, a]})#r] {
	def point[A](a: => A) = Continuation { k => k(a) }
	def bind[A, B](fa: Continuation[R, A])(f: (A) => Continuation[R, B]) = {
		Continuation { k =>
			fa.runCont { a =>
				f(a).runCont(k)
			}
		}
	}
}

trait ContinuationFunctions {
	def callCC[R, A, B](f: (A => Continuation[R, B]) => Continuation[R, A]): Continuation[R, A] = {
		Continuation { k =>
			f { a =>
				Continuation { _ => k(a) }
			}.runCont(k)
		}
	}
}

trait ContinuationInstances {
	implicit def continuationInstance[R] = new ContinuationMonad[R] {
	}
}

case object Continuation extends ContinuationFunctions with ContinuationInstances
