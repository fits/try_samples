package fits.sample

import scalaz._
import Scalaz._

object WriterSample2 extends App {

	val logNum = (n: Int) => (x: Int) => WriterT.writer (s" + $n", n + x)

	val r = for {
		w <- WriterT.writer ("2", 2)
		w2 <- logNum(5)(w)
		w3 <- logNum(3)(w2)
	} yield w3

	println(s"${r.written} = ${r.value}")
}
