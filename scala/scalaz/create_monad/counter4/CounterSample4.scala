package fits.sample

import scalaz._
import Scalaz._

case class Counter[C, A](val count: (A, C))

trait CounterMonad[C] extends Monad[({type c[a] = Counter[C, a]})#c] {
	implicit def C0: Monoid[C]

	def point[A](a: => A) = Counter( (a, C0.zero) )
	def bind[A, B](fa: Counter[C, A])(f: (A) => Counter[C, B]) = {
		val (a, c) = fa.count
		val (b, d) = f(a).count
		Counter( (b, c mappend d) )
	}
}

trait CounterFunctions {
	def counter[C, A](x: A, i: C): Counter[C, A] = Counter( (x, i) )
}

trait CounterInstances {
	implicit def counterInstance[C](implicit C1: Monoid[C]) = new CounterMonad[C] {
		implicit def C0 = C1
	}
}

object Counter extends CounterFunctions with CounterInstances {
	def apply[C, A](x: A, i: C) = counter(x, i)
}

object CounterSample4 extends App {
	val countUp = (s: String) => (x: String) => Counter(x + s, 1)

	val point = (x: String) => Counter(x, 1)
	// (a,1)
	point("a").count |> println
	// (ab,2)
	( point("a") >>= countUp("b") ).count |> println
	// (abc,3)
	( point("a") >>= countUp("b") >>= countUp("c") ).count |> println
}
