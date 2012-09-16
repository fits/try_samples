package fits.sample

import scalaz._
import Scalaz._
import WriterT._

/**
 * WriterT に List を適用したサンプル
 *
 * ログ部分と値部分で別途 List として管理される
 */
object WriterSample3 extends App {

	val logNumList: Int => Int => WriterT[List, String, Int] = (n: Int) => (x: Int) => {
		val list = for {
			i <- (1 to n).toList
		} yield (s" + $i", x + i)

		writerT(list)
	}

	val w = writerT(List(("2", 2))) >>= logNumList(3) >>= logNumList(2)

	/* ログ部分のリストを出力
	 *
	 * log = List(2 + 1 + 1, 2 + 1 + 2, 2 + 2 + 1, 2 + 2 + 2, 
	 *             2 + 3 + 1, 2 + 3 + 2)
	 */
	println(s"log = ${w.written}")

	/* 値部分のリストを出力
	 *
	 * value = List(4, 5, 5, 6, 6, 7)
	 */
	println(s"value = ${w.value}")
}
