package fits.sample

import scalaz._
import Scalaz._

case class Counter[A](val count: (A, Int))

trait CounterInstances {
	implicit val counterInstance = new Monad[Counter] {
		def point[A](a: => A): Counter[A] = Counter (a, 1)
		def bind[A, B](fa: Counter[A])(f: (A) => Counter[B]): Counter[B] = {
			val (a, c) = fa.count
			val (b, _) = f(a).count
			Counter (b, c + 1)
		}
	}
	implicit def counterShow[A: Show]: Show[Counter[A]] = new Show[Counter[A]] {
		override def show(as: Counter[A]) = as.count.show
	}
}

case object Counter extends CounterInstances

object CounterSample2 extends App {
	import Counter.counterInstance.point

	val countUp = (s: String) => (x: String) => point(x + s)

	point("a").show |> println

	( point("a") >>= countUp("b") ).show |> println

	( point("a") >>= countUp("b") >>= countUp("c") ).show |> println
}
