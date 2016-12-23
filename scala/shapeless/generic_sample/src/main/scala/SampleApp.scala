
import shapeless.Generic

sealed trait A

case class B(name: String) extends A
case class C(name: String, value: Int) extends A
case class D() extends A

object SampleApp extends App {
  val genB = Generic[B]
  val genC = Generic[C]

  println(genB)

  // HList

  val b1 = B("b1")
  val bg1 = genB.to(b1) // b1 :: HNil

  println(b1)
  println(bg1)

  val c1 = C("c1", 10)
  val cg1 = genC.to(c1) // c1 :: 10 :: HNil

  println(c1)
  println(cg1)
  println(c1 == genC.from(cg1))

  println("-----")

  // Coproduct (like Either)

  val genA = Generic[A]
  val b1a = genA.to(b1) // Inl(B(b1))
  val c1a = genA.to(c1) // Inr(Inl(C(c1,10)))

  println(b1a)
  println(genA.from(b1a))

  println(c1a)
  println(genA.from(c1a))
}
