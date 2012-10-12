package fits.sample

import scalaz._
import Scalaz._
import WriterT._

object WriterSample extends App {

	val logNum = (n: Int) => (x: Int) => writer (s" + $n", n + x)

	val r1 = ("2", 2) |> writer >>= logNum(5) >>= logNum(3)
	// 以下でも可
	//val r1 = writer ("2", 2) >>= logNum(5) >>= logNum(3)

	println(s"${r1.written} = ${r1.value}")

	// for を使っても同じ
	val r2 = for {
		w1 <- writer ("2", 2)
		w2 <- logNum(5)(w1)
		w3 <- logNum(3)(w2)
	} yield w3

	println(s"${r2.written} = ${r2.value}")


	val logNumList = (n: Int) => (x: Int) => writer (List(n), n + x)

	val r3 = (List(2), 2) |> writer >>= logNumList(5) >>= logNumList(3)
	// 以下でも可
	//val r3 = writer (List(2), 2) >>= logNumList(5) >>= logNumList(3)

	println(s"${r3.written} = ${r3.value}")
}
