package fits.sample

import scalaz._
import Scalaz._

case class Counter[C, A](val count: (A, C))

trait CounterInstances {
	implicit def counterInstance[C](implicit C0: Monoid[C]) = new Monad[({type c[a] = Counter[C, a]})#c] {

		def point[A](a: => A) = Counter (a, C0.zero)
		def bind[A, B](fa: Counter[C, A])(f: (A) => Counter[C, B]) = {
			val (a, c) = fa.count
			val (b, d) = f(a).count
			Counter (b, c mappend d)
		}
	}
}

case object Counter extends CounterInstances

object CounterSample3 extends App {
	val countUp = (s: String) => (x: String) => Counter (x + s, 1)

	val point = (x: String) => Counter.counterInstance(intInstance).point(x)
	// (a,0)
	point("a").count |> println
	// (ab,1)
	( point("a") >>= countUp("b") ).count |> println
	// (abc,2)
	( point("a") >>= countUp("b") >>= countUp("c") ).count |> println

	println("-----")

	val point2 = (x: String) => Counter (x, 1)
	// (a,1)
	point2("a").count |> println
	// (ab,2)
	( point2("a") >>= countUp("b") ).count |> println
	// (abc,3)
	( point2("a") >>= countUp("b") >>= countUp("c") ).count |> println
}
