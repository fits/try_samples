
import cats.implicits._
import cats.data.Writer

object SampleApp extends App {
  val printWriter = (w: Writer[String, Int]) =>
            println(s"written = ${w.written}, value = ${w.value}")

  val logNum = (n: Int) => (x: Int) => Writer(s" + $n", n + x)

  val r = Writer("2", 2) flatMap logNum(5) flatMap logNum(3)

  printWriter(r)

  val r2 = r >>= logNum(7)

  printWriter(r2)

  // Compile Error "value >>= is not a member of cats.data.WriterT[[A]A,String,Int]"
  // val r3 = r >>= logNum(7) >>= logNum(8)

  val r3 = ((r >>= logNum(7)): Writer[String, Int]) >>= logNum(8)

  printWriter(r3)
}
