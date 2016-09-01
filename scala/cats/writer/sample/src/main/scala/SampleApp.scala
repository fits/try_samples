
import cats.implicits._
import cats.data.Writer

object SampleApp extends App {
  val logNum = (n: Int) => (x: Int) => Writer(s" + $n", n + x)

  val r = Writer("2", 2) flatMap logNum(5) flatMap logNum(3)

  //  compile error
  // val r = Writer("2", 2) >>= logNum(5) >>= logNum(3)

  println(s"written = ${r.written}, value = ${r.value}")
}
