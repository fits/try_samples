package fits.sample

import scalaz._
import Scalaz._

object WriterSample extends App {

	val logNum = (n: Int) => (x: Int) => WriterT.writer (s" + $n", n + x)

	val r = WriterT.writer ("2", 2) >>= logNum(5) >>= logNum(3)

	println(s"${r.written} = ${r.value}")
}
