
import shapeless.{Poly1, everywhere}

sealed trait A

case class B(name: String) extends A
case class C(name: String, value: Int) extends A

object Plus extends Poly1 {
  implicit def caseInt = at[Int](_ + 10)
  implicit def caseString = at[String](s => s"-${s}-")
}

object SampleApp extends App {

  val b1 = B("b1")
  val c1 = C("c1", 10)

  val f1 = everywhere(Plus)

  println(f1)

  println(f1(b1))
  println(f1(c1))
}
