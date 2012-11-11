package fits.sample

import scalaz._
import Scalaz._

// (1) モナドとして扱う型を定義
case class Counter[A](val count: (A, Int))

// (2) Monad のインスタンスを定義
trait CounterInstances {
	implicit val counterInstance = new Monad[Counter] {
		def point[A](x: => A): Counter[A] = Counter (x, 1)
		def bind[A, B](fa: Counter[A])(f: (A) => Counter[B]): Counter[B] = {
			val (x, c) = fa.count
			val (y, _) = f(x).count
			Counter (y, c + 1)
		}
	}
}
// (2) Monad のインスタンスを定義
case object Counter extends CounterInstances

// Counter モナドの利用
object CounterSample extends App {
	import Counter.counterInstance.point

	val append = (s: String) => (x: String) => point(x + s)

	// (a, 1)
	point("a").count |> println
	// (ab, 2)
	( point("a") >>= append("b") ).count |> println
	// (abc, 3)
	( point("a") >>= append("b") >>= append("c") ).count |> println
}
