package sample

import cats.Show

case class Value(v: Int = 0)

object Logger {
  def info[A](d: A)(implicit F: Show[A]): Unit = println(F.show(d))
  def info2[A: Show](d: A): Unit = println(implicitly[Show[A]].show(d))
}

object SampleApp extends App {
  implicit val showValue: Show[Value] = d => s"Value(v = ${d.v})"

  Logger.info(Value(1))
  Logger.info2(Value(2))
}
