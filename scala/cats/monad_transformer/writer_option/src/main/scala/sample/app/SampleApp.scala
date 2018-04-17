package sample.app

import cats.Monad
import cats.data.WriterT
import cats.implicits._

object SampleApp extends App {

  type WriterOption[A] = WriterT[Option, String, A]

  def logNum(n: Int)(x: Int): WriterOption[Int] = WriterT.put(x + n)(s"$x ")
  def plus(n: Int)(x: Int): WriterOption[Int] = WriterT.value(x + n)

  def none: WriterOption[Int] = WriterT.valueT[Option, String, Int](None)
  def value(n: Int): WriterOption[Int] = WriterT.value(n)
  def value2(n: Int) = Monad[WriterOption].pure(n)

  println( value(10).run )
  println( value2(10).run )

  // Some((10 12 ,15))
  println( value(10).flatMap(logNum(2)).flatMap(logNum(3)).run )
  // Some((10 12 ,15))
  println( value2(10).flatMap(logNum(2)).flatMap(logNum(3)).run )

  // Some((10 ,15))
  println( value(10).flatMap(logNum(2)).flatMap(plus(3)).run )
  // Some((10 15 ,19))
  println( value(10).flatMap(logNum(2)).flatMap(plus(3)).flatMap(logNum(4)).run )

  // None
  println( none.run )
  // None
  println( none.flatMap(logNum(2)).flatMap(plus(3)).run )
  // None
  println( value(10).flatMap(logNum(2)).flatMap(_ => none).run )
}
