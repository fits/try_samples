package fits.sample

import scala.concurrent._
import duration.Duration
import ExecutionContext.Implicits.global

object Sample2 extends App {
	val f: Future[Int] = future {
		Thread.sleep(2000)
		10
	}

	val ferr: Future[Int] = future {
		throw new Exception()
	}

	val f2 = (s: Int) => (t: Int) => future {
		Thread.sleep(1000)
		s + t
	}

	val fr1 = f.flatMap {
		f2(5)
	} map {
		r => println(s"res : $r")
	}

	Await.ready(fr1, Duration.Inf)

	val fr2 = for {
		x <- f
		y <- f2(5)(x)
	} yield println(s"res2 : $y")

	Await.ready(fr2, Duration.Inf)

	/** 
	 * ferr で失敗するため f2 は実行されず
	 * "res3 : " も出力されない
	 */
	val fr3 = for {
		x <- ferr
		y <- f2(5)(x)
	} yield println(s"res3 : $y")

	Await.ready(fr3, Duration.Inf)
}
