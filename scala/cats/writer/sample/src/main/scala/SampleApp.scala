
import cats.implicits._
import cats.data.Writer

object SampleApp extends App {
  val logNum = (n: Int) => (x: Int) => Writer(s" + $n", n + x)

  val r = Writer("2", 2) flatMap logNum(5) flatMap logNum(3)

  println(s"written = ${r.written}, value = ${r.value}")

  val r2 = r >>= logNum(7)

  println(s"written = ${r2.written}, value = ${r2.value}")

  // compile error
  // val r3 = r2 >>= logNum(8)

}
