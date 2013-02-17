package fits.sample

import scalaz._
import Scalaz._

// (1) モナドとして扱う型を定義
case class Counter[A](count: (A, Int))

// (2) Monad のインスタンスを定義
trait CounterInstances {
	implicit val counterInstance = new Monad[Counter] {
		def point[A](x: => A): Counter[A] = Counter (x, 0)
		def bind[A, B](fa: Counter[A])(f: (A) => Counter[B]): Counter[B] = {
			val (x, c) = fa.count
			val (y, d) = f(x).count
			Counter (y, c + d)
		}
	}
}
// (2) Monad のインスタンスを定義
case object Counter extends CounterInstances

// Counter モナドの利用
object CounterSample extends App {
	import Counter.counterInstance.point

	val append = (s: String) => (x: String) => Counter (x + s, 1)

	// (a, 0)
	point("a").count |> println
	
	// (ab, 1) 左恒等性
	( point("a") >>= append("b") ).count |> println
	( append("b")("a") ).count |> println

	// (abc, 2)
	( point("a") >>= append("b") >>= append("c") ).count |> println

	// (d, 3) 右恒等性
	( Counter ("d", 3) >>= { s: String => point(s) } ).count |> println
}
