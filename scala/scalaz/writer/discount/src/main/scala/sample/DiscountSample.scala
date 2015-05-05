package sample

import scalaz._
import Scalaz._

object DiscountSample extends App {

  val discount = (d: Int) => (c: Int) => Writer(List(d), c - d)

	val p = Writer(List.empty[Int], 1000)

	println(p)

	val r = p flatMap discount(100) flatMap discount(432)
  // 下記ではコンパイルエラーが発生？？？ value >>= is not a member of scalaz.WriterT[[+X]X,List[Int],Int]
  //val r = p >>= discount(100) >>= discount(432)

  println(r)
}
