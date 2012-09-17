package fits.sample

import scalaz._
import Scalaz._
import WriterT._

/**
 * WriterT に List を適用したサンプル
 */
object WriterTSample extends App {
	//  Int => Int => WriterT[List, String, Int]
	val logManyNum = (n: Int) => (x: Int) => {
		val list = for {
			i <- (1 to n).toList
		} yield (s" + $i", x + i)

		writerT(list)
	}

	val w = writerT(("2", 2) :: Nil) >>= logManyNum(3) >>= logManyNum(2)
	//以下でも同じ
	//val w = writerT(List(("2", 2))) >>= logManyNum(3) >>= logManyNum(2)

	println(s"written = ${w.written}")
	println(s"value = ${w.value}")
	println(s"run = ${w.run}")
}
