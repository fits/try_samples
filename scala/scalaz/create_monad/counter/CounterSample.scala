package fits.sample

import scalaz._
import Scalaz._

case class Counter[A](val count: (A, Int))

trait CounterInstances {
	implicit val counterInstance = new Monad[Counter] {
		def point[A](a: => A): Counter[A] = Counter (a, 0)
		def bind[A, B](fa: Counter[A])(f: (A) => Counter[B]): Counter[B] = {
			val (a, c) = fa.count
			val (b, d) = f(a).count
			Counter (b, c + d)
		}
	}
}

case object Counter extends CounterInstances

object CounterSample extends App {
	import Counter.counterInstance.point

	val countUp = (s: String) => (x: String) => Counter (x + s, 1)

	point("a").count |> println

	( point("a") >>= countUp("b") ).count |> println
	( countUp("b")("a") ).count |> println

	( point("a") >>= countUp("b") >>= countUp("c") ).count |> println
}
